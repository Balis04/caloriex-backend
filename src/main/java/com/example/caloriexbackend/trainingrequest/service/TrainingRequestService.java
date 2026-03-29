package com.example.caloriexbackend.trainingrequest.service;

import com.example.caloriexbackend.coachprofile.service.CoachProfileService;
import com.example.caloriexbackend.trainingrequest.email.TrainingRequestEmailService;
import com.example.caloriexbackend.common.enums.TrainingRequestStatus;
import com.example.caloriexbackend.common.exception.BadRequestException;
import com.example.caloriexbackend.common.exception.NotFoundException;
import com.example.caloriexbackend.common.security.AuthenticatedUserService;
import com.example.caloriexbackend.entities.CoachProfile;
import com.example.caloriexbackend.entities.TrainingPlan;
import com.example.caloriexbackend.entities.TrainingRequest;
import com.example.caloriexbackend.entities.User;
import com.example.caloriexbackend.trainingrequest.mapper.TrainingRequestMapper;
import com.example.caloriexbackend.trainingrequest.payload.request.TrainingRequestCreateRequest;
import com.example.caloriexbackend.trainingrequest.payload.request.TrainingRequestStatusUpdateRequest;
import com.example.caloriexbackend.trainingrequest.payload.response.ClosedTrainingRequestResponse;
import com.example.caloriexbackend.trainingrequest.repository.TrainingRequestRepository;
import com.example.caloriexbackend.trainingrequest.trainingplan.payload.TrainingPlanResponse;
import com.example.caloriexbackend.trainingrequest.payload.response.TrainingRequestResponse;
import com.example.caloriexbackend.trainingrequest.trainingplan.mapper.TrainingPlanMapper;
import com.example.caloriexbackend.trainingrequest.trainingplan.repository.TrainingPlanRepository;
import com.example.caloriexbackend.trainingrequest.trainingplan.service.TrainingPlanService;
import com.example.caloriexbackend.validation.TrainingRequestValidator;
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
    private final AuthenticatedUserService authenticatedUserService;
    private final TrainingPlanRepository trainingPlanRepository;
    private final TrainingRequestEmailService emailService;
    private final TrainingRequestMapper trainingRequestMapper;
    private final TrainingPlanMapper trainingPlanMapper;
    private final TrainingRequestValidator validator;
    private final TrainingPlanService trainingPlanService;
    private final CoachProfileService coachProfileService;

    @Transactional
    public TrainingRequestResponse create(UUID coachProfileId, TrainingRequestCreateRequest request) {
        User requester = authenticatedUserService.getUser();

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
        trainingRequest.setCoachResponse(request.coachResponse());

        emailService.sendStatusUpdateEmail(trainingRequest);

        return trainingRequestMapper.toResponse(trainingRequest);
    }

    @Transactional
    public TrainingPlanResponse uploadTrainingPlan(
            UUID trainingRequestId,
            MultipartFile file,
            String planName,
            String planDescription
    ) {
        TrainingRequest trainingRequest = findTrainingRequest(trainingRequestId);

        boolean planExists = trainingPlanRepository.existsByTrainingRequestId(trainingRequestId);

        validator.validateUpload(trainingRequest, planExists);

        TrainingPlan trainingPlan = trainingPlanService.create(
                trainingRequest,
                file,
                planName,
                planDescription
        );

        trainingRequest.setStatus(TrainingRequestStatus.CLOSED);
        emailService.sendTrainingPlanUploadedEmail(trainingRequest, trainingPlan);

        return trainingPlanMapper.toResponse(trainingPlan);
    }

    @Transactional(readOnly = true)
    public List<TrainingRequestResponse> getMyRequests() {
        User requester = authenticatedUserService.getUser();
        return trainingRequestRepository.findAllByRequesterUserIdOrderByCreatedAtDesc(requester.getId()).stream()
                .map(trainingRequestMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TrainingRequestResponse> getRequestsForMyCoachProfile(TrainingRequestStatus status) {
        User coachUser = authenticatedUserService.getUser();

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
        User coachUser = authenticatedUserService.getUser();

        return trainingPlanRepository
                .findAllByTrainingRequestCoachProfileUserIdOrderByUploadedAtDesc(coachUser.getId())
                .stream()
                .map(trainingRequestMapper::toClosedResponse)
                .toList();
    }

    private TrainingRequest findTrainingRequest(UUID trainingRequestId){
        User coachUser = authenticatedUserService.getUser();
        return trainingRequestRepository.findByIdAndCoachProfileUserId(trainingRequestId, coachUser.getId())
                .orElseThrow(() -> new NotFoundException("Training request not found"));
    }
}
