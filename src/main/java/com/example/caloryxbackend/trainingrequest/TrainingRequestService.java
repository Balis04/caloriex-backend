package com.example.caloryxbackend.trainingrequest;

import com.example.caloryxbackend.account.CurrentUserService;
import com.example.caloryxbackend.coachprofile.CoachProfileService;
import com.example.caloryxbackend.common.email.TrainingRequestEmailService;
import com.example.caloryxbackend.common.exception.BadRequestException;
import com.example.caloryxbackend.common.exception.NotFoundException;
import com.example.caloryxbackend.entities.CoachProfile;
import com.example.caloryxbackend.entities.TrainingPlan;
import com.example.caloryxbackend.entities.TrainingRequest;
import com.example.caloryxbackend.entities.User;
import com.example.caloryxbackend.common.enums.TrainingRequestStatus;
import com.example.caloryxbackend.trainingrequest.payload.request.TrainingRequestCreateRequest;
import com.example.caloryxbackend.trainingrequest.payload.request.TrainingRequestStatusUpdateRequest;
import com.example.caloryxbackend.trainingrequest.payload.response.ClosedTrainingRequestResponse;
import com.example.caloryxbackend.trainingrequest.payload.response.TrainingPlanResponse;
import com.example.caloryxbackend.trainingrequest.payload.response.TrainingRequestResponse;
import com.example.caloryxbackend.trainingrequest.trainingplan.TrainingPlanMapper;
import com.example.caloryxbackend.trainingrequest.trainingplan.TrainingPlanRepository;
import com.example.caloryxbackend.trainingrequest.trainingplan.TrainingPlanService;
import com.example.caloryxbackend.validation.TrainingRequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrainingRequestService {

    private final TrainingRequestRepository trainingRequestRepository;
    private final CurrentUserService currentUserService;
    private final TrainingPlanRepository trainingPlanRepository;
    private final TrainingRequestEmailService emailService;
    private final TrainingRequestMapper trainingRequestMapper;
    private final TrainingPlanMapper trainingPlanMapper;
    private final TrainingRequestValidator validator;
    private final TrainingPlanService trainingPlanService;
    private final CoachProfileService coachProfileService;

    @Transactional
    public TrainingRequestResponse create(UUID coachProfileId, TrainingRequestCreateRequest request) {
        User requester = currentUserService.getUser();

        CoachProfile coachProfile = coachProfileService.findCoachProfile(coachProfileId);

        if (coachProfile.getUser().getId().equals(requester.getId())) {
            throw new BadRequestException("You cannot send a training request to your own coach profile");
        }

        TrainingRequest trainingRequest = trainingRequestMapper.toEntity(request, TrainingRequestStatus.PENDING, requester, coachProfile);

        TrainingRequest saved = trainingRequestRepository.save(trainingRequest);

        emailService.sendCreationEmail(saved, requester, coachProfile);

        return trainingRequestMapper.toResponse(saved);
    }

    @Transactional
    public TrainingRequestResponse updateStatus(UUID trainingRequestId, TrainingRequestStatusUpdateRequest request) {
        TrainingRequest trainingRequest = findTrainingRequest(trainingRequestId);

        validator.validateStatusUpdate(trainingRequest.getStatus(), request.status());

        trainingRequest.setStatus(request.status());
        trainingRequest.setDescription(request.description());

        emailService.sendStatusUpdateEmail(trainingRequest);

        return trainingRequestMapper.toResponse(trainingRequest);
    }

    @Transactional
    public TrainingPlanResponse uploadTrainingPlan(
            UUID trainingRequestId,
            MultipartFile file,
            String planName,
            String description
    ) {
        TrainingRequest trainingRequest = findTrainingRequest(trainingRequestId);

        boolean planExists = trainingPlanRepository.existsByTrainingRequestId(trainingRequestId);

        validator.validateUpload(trainingRequest, planExists);

        TrainingPlan trainingPlan = trainingPlanService.create(
                trainingRequest,
                file,
                planName,
                description
        );

        trainingRequest.setStatus(TrainingRequestStatus.CLOSED);

        return trainingPlanMapper.toResponse(trainingPlan);
    }

    @Transactional(readOnly = true)
    public List<TrainingRequestResponse> getMyRequests() {
        User requester = currentUserService.getUser();
        return trainingRequestRepository.findAllByRequesterUserIdOrderByCreatedAtDesc(requester.getId()).stream()
                .map(trainingRequestMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TrainingRequestResponse> getRequestsForMyCoachProfile(TrainingRequestStatus status) {
        User coachUser = currentUserService.getUser();

        if (status == null) {
            return trainingRequestRepository
                    .findAllByCoachProfileUserIdOrderByCreatedAtDesc(coachUser.getId())
                    .stream()
                    .map(trainingRequestMapper::toResponse)
                    .toList();
        }

        return trainingRequestRepository
                .findAllByCoachProfileUserIdAndStatusOrderByCreatedAtDesc(
                        coachUser.getId(),
                        status
                ).stream()
                .map(trainingRequestMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ClosedTrainingRequestResponse> getClosedRequestsForMyCoachProfile() {
        User coachUser = currentUserService.getUser();

        return trainingPlanRepository
                .findAllByCoachUserIdOrderByUploadedAtDesc(coachUser.getId())
                .stream()
                .map(trainingRequestMapper::toClosedResponse)
                .toList();
    }

    private TrainingRequest findTrainingRequest(UUID trainingRequestId){
        User coachUser = currentUserService.getUser();
        return trainingRequestRepository.findByIdAndCoachProfileUserId(trainingRequestId, coachUser.getId())
                .orElseThrow(() -> new NotFoundException("Training request not found"));
    }
}
