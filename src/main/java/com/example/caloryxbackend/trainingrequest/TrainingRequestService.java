package com.example.caloryxbackend.trainingrequest;

import com.example.caloryxbackend.account.CurrentUserService;
import com.example.caloryxbackend.coachprofile.CoachProfileRepository;
import com.example.caloryxbackend.common.exception.BadRequestException;
import com.example.caloryxbackend.common.exception.NotFoundException;
import com.example.caloryxbackend.entities.CoachProfile;
import com.example.caloryxbackend.entities.TrainingRequest;
import com.example.caloryxbackend.entities.User;
import com.example.caloryxbackend.trainingrequest.model.TrainingRequestStatus;
import com.example.caloryxbackend.trainingrequest.payload.TrainingRequestCreateRequest;
import com.example.caloryxbackend.trainingrequest.payload.TrainingRequestResponse;
import com.example.caloryxbackend.trainingrequest.payload.TrainingRequestStatusUpdateRequest;
import com.example.caloryxbackend.user.UserRepository;
import com.example.caloryxbackend.user.model.enums.ActivityLevel;
import com.example.caloryxbackend.user.model.enums.GoalType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrainingRequestService {

    private final TrainingRequestRepository trainingRequestRepository;
    private final CoachProfileRepository coachProfileRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final JavaMailSender mailSender;

    @Value("${app.mail.training-request.from:${spring.mail.username:}}")
    private String fromAddress;

    @Transactional
    public TrainingRequestResponse create(UUID coachProfileId, TrainingRequestCreateRequest request) {
        User requester = getCurrentUser();
        CoachProfile coachProfile = coachProfileRepository.findById(coachProfileId)
                .orElseThrow(() -> new NotFoundException("Coach profile not found"));

        if (coachProfile.getUser().getId().equals(requester.getId())) {
            throw new BadRequestException("You cannot send a training request to your own coach profile");
        }

        String coachEmail = trimToNull(coachProfile.getUser().getEmail());
        if (coachEmail == null) {
            throw new BadRequestException("Selected coach does not have an email address");
        }

        TrainingRequest trainingRequest = new TrainingRequest();
        trainingRequest.setRequesterUser(requester);
        trainingRequest.setCoachProfile(coachProfile);
        trainingRequest.setWeeklyTrainingCount(request.weeklyTrainingCount());
        trainingRequest.setSessionDurationMinutes(request.sessionDurationMinutes());
        trainingRequest.setPreferredLocation(request.preferredLocation().trim());
        trainingRequest.setStatus(request.status() == null ? TrainingRequestStatus.PENDING : request.status());
        trainingRequest.setCoachNote(trimToNull(request.coachNote()));

        TrainingRequest saved = trainingRequestRepository.save(trainingRequest);

        sendCreationNotificationEmail(saved, requester, coachProfile, coachEmail);

        return mapResponse(saved);
    }

    @Transactional
    public TrainingRequestResponse updateStatus(UUID trainingRequestId, TrainingRequestStatusUpdateRequest request) {
        User coachUser = getCurrentUser();
        TrainingRequest trainingRequest = trainingRequestRepository.findByIdAndCoachProfileUserId(trainingRequestId, coachUser.getId())
                .orElseThrow(() -> new NotFoundException("Training request not found"));

        TrainingRequestStatus newStatus = request.status();
        if (newStatus == TrainingRequestStatus.PENDING) {
            throw new BadRequestException("Status can only be APPROVED or REJECTED");
        }

        trainingRequest.setStatus(newStatus);

        String requesterEmail = trimToNull(trainingRequest.getRequesterUser().getEmail());
        if (requesterEmail == null) {
            throw new BadRequestException("Requester does not have an email address");
        }

        sendStatusUpdateEmail(trainingRequest, requesterEmail);

        return mapResponse(trainingRequest);
    }

    @Transactional(readOnly = true)
    public List<TrainingRequestResponse> getMyRequests() {
        User requester = getCurrentUser();
        return trainingRequestRepository.findAllByRequesterUserIdOrderByCreatedAtDesc(requester.getId()).stream()
                .map(this::mapResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TrainingRequestResponse> getRequestsForMyCoachProfile() {
        User coachUser = getCurrentUser();
        return trainingRequestRepository.findAllByCoachProfileUserIdOrderByCreatedAtDesc(coachUser.getId()).stream()
                .map(this::mapResponse)
                .toList();
    }

    private User getCurrentUser() {
        String auth0Id = currentUserService.getAuth0Id();
        return userRepository.findByAuth0id(auth0Id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private void sendCreationNotificationEmail(
            TrainingRequest trainingRequest,
            User requester,
            CoachProfile coachProfile,
            String coachEmail
    ) {
        SimpleMailMessage message = createBaseMailMessage();
        message.setTo(coachEmail);
        message.setSubject("Uj edzesterv keres erkezett - " + formatValue(requester.getFullName()));

        String replyTo = trimToNull(requester.getEmail());
        if (replyTo != null) {
            message.setReplyTo(replyTo);
        }

        message.setText(buildCreationMailBody(trainingRequest, requester, coachProfile));
        mailSender.send(message);
    }

    private void sendStatusUpdateEmail(TrainingRequest trainingRequest, String requesterEmail) {
        SimpleMailMessage message = createBaseMailMessage();
        message.setTo(requesterEmail);
        message.setSubject("A training keresed statusza frissult - " + formatStatus(trainingRequest.getStatus()));
        message.setText(buildStatusUpdateMailBody(trainingRequest));
        mailSender.send(message);
    }

    private SimpleMailMessage createBaseMailMessage() {
        SimpleMailMessage message = new SimpleMailMessage();
        if (fromAddress != null && !fromAddress.isBlank()) {
            message.setFrom(fromAddress);
        }
        return message;
    }

    private String buildCreationMailBody(TrainingRequest trainingRequest, User requester, CoachProfile coachProfile) {
        String coachName = formatValue(coachProfile.getUser().getFullName());

        return """
                Uj edzesterv keres erkezett.

                Edzo: %s

                Ugyfel adatai:
                - Nev: %s
                - Email: %s
                - Jelenlegi testsuly: %s
                - Cel testsuly: %s
                - Cel: %s
                - Aktivitasi szint: %s

                Edzesi igenyek:
                - Statusz: %s
                - Heti edzesek szama: %s
                - Egy alkalom hossza: %s perc
                - Preferalt helyszin: %s
                - Leiras: %s

                Keres azonosito: %s
                Letrehozva: %s
                """.formatted(
                coachName,
                formatValue(requester.getFullName()),
                formatValue(requester.getEmail()),
                formatWeight(requester.getActualWeightKg()),
                formatWeight(requester.getTargetWeightKg()),
                formatGoal(requester.getGoal()),
                formatActivityLevel(requester.getActivityLevel()),
                trainingRequest.getStatus(),
                trainingRequest.getWeeklyTrainingCount(),
                trainingRequest.getSessionDurationMinutes(),
                formatValue(trainingRequest.getPreferredLocation()),
                formatValue(trainingRequest.getCoachNote()),
                trainingRequest.getId(),
                trainingRequest.getCreatedAt()
        );
    }

    private String buildStatusUpdateMailBody(TrainingRequest trainingRequest) {
        return """
                A training keresed statusza megvaltozott.

                Uj statusz: %s
                Edzo: %s
                Heti edzesszam: %s
                Alkalom hossza: %s perc
                Preferalt helyszin: %s
                Keres azonosito: %s
                Letrehozva: %s
                """.formatted(
                formatStatus(trainingRequest.getStatus()),
                formatValue(trainingRequest.getCoachProfile().getUser().getFullName()),
                trainingRequest.getWeeklyTrainingCount(),
                trainingRequest.getSessionDurationMinutes(),
                formatValue(trainingRequest.getPreferredLocation()),
                trainingRequest.getId(),
                trainingRequest.getCreatedAt()
        );
    }

    private TrainingRequestResponse mapResponse(TrainingRequest trainingRequest) {
        return new TrainingRequestResponse(
                trainingRequest.getId(),
                trainingRequest.getCoachProfile().getId(),
                trainingRequest.getRequesterUser().getId(),
                trainingRequest.getCoachProfile().getUser().getFullName(),
                trainingRequest.getRequesterUser().getFullName(),
                trainingRequest.getRequesterUser().getEmail(),
                trainingRequest.getWeeklyTrainingCount(),
                trainingRequest.getSessionDurationMinutes(),
                trainingRequest.getPreferredLocation(),
                trainingRequest.getStatus(),
                trainingRequest.getCoachNote(),
                trainingRequest.getCreatedAt()
        );
    }

    private String formatWeight(Double weight) {
        if (weight == null) {
            return "nincs megadva";
        }
        return weight + " kg";
    }

    private String formatGoal(GoalType goal) {
        if (goal == null) {
            return "nincs megadva";
        }

        return switch (goal) {
            case CUT -> "Fogyas";
            case MAINTAIN -> "Sulytartas";
            case BULK -> "Tomegnoveles";
        };
    }

    private String formatActivityLevel(ActivityLevel activityLevel) {
        if (activityLevel == null) {
            return "nincs megadva";
        }

        return switch (activityLevel) {
            case SEDENTARY -> "Ulo eletmod";
            case LIGHT -> "Konnyu aktivitas";
            case MODERATE -> "Kozepes aktivitas";
            case ACTIVE -> "Magas aktivitas";
        };
    }

    private String formatStatus(TrainingRequestStatus status) {
        return switch (status) {
            case PENDING -> "Folyamatban";
            case APPROVED -> "Elfogadva";
            case REJECTED -> "Elutasitva";
        };
    }

    private String formatValue(String value) {
        String trimmed = trimToNull(value);
        return trimmed == null ? "nincs megadva" : trimmed;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
