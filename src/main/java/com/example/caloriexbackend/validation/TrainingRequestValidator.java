package com.example.caloriexbackend.validation;

import com.example.caloriexbackend.common.enums.TrainingRequestStatus;
import com.example.caloriexbackend.common.exception.BadRequestException;
import com.example.caloriexbackend.entities.TrainingRequest;
import org.springframework.stereotype.Component;

@Component
public class TrainingRequestValidator {

    public void validateUpload(TrainingRequest request, boolean planExists) {
        if (request.getStatus() == TrainingRequestStatus.CLOSED) {
            throw new BadRequestException("Training request is already closed");
        }

        if (request.getStatus() != TrainingRequestStatus.APPROVED) {
            throw new BadRequestException("Training plan can only be uploaded for approved requests");
        }

        if (planExists) {
            throw new BadRequestException("Training plan already exists for this request");
        }
    }

    public void validateStatusUpdate(TrainingRequestStatus actualStatus ,TrainingRequestStatus nextStatus){
        if (nextStatus != TrainingRequestStatus.APPROVED && nextStatus != TrainingRequestStatus.REJECTED) {
            throw new BadRequestException("Status can only be APPROVED or REJECTED");
        }

        if (actualStatus.equals(TrainingRequestStatus.CLOSED)){
            throw new BadRequestException("Actual status can not be CLOSED");
        }
    }
}
