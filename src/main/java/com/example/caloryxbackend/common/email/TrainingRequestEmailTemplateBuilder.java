package com.example.caloryxbackend.common.email;

import com.example.caloryxbackend.entities.CoachProfile;
import com.example.caloryxbackend.entities.TrainingRequest;
import com.example.caloryxbackend.entities.User;
import org.springframework.stereotype.Component;

@Component
public class TrainingRequestEmailTemplateBuilder {

    public String buildCreationMailBody(TrainingRequest trainingRequest, User requester, CoachProfile coachProfile) {
        return """
                Új edzésterv kérés érkezett a Caloryx rendszerben.

                Edző: %s

                Ügyfél adatai:
                - Név: %s
                - Email: %s
                - Jelenlegi testsúly: %s
                - Cél testsúly: %s
                - Cél: %s
                - Aktivitási szint: %s

                Edzési igények:
                - Heti edzések száma: %s
                - Egy alkalom hossza: %s perc
                - Preferált helyszín: %s
                - Megjegyzés: %s

                Kérés státusza: %s
                Kérés azonosító: %s
                Létrehozva: %s

                A részletekért lépj be az alkalmazásba.
                """.formatted(
                EmailFormatUtils.safe(coachProfile.getUser().getFullName()),
                EmailFormatUtils.safe(requester.getFullName()),
                EmailFormatUtils.safe(requester.getEmail()),
                EmailFormatUtils.weight(requester.getActualWeightKg()),
                EmailFormatUtils.weight(requester.getTargetWeightKg()),
                EmailFormatUtils.goal(requester.getGoal()),
                EmailFormatUtils.activityLevel(requester.getActivityLevel()),
                trainingRequest.getWeeklyTrainingCount(),
                trainingRequest.getSessionDurationMinutes(),
                EmailFormatUtils.safe(trainingRequest.getPreferredLocation()),
                EmailFormatUtils.safe(trainingRequest.getCoachNote()),
                EmailFormatUtils.status(trainingRequest.getStatus()),
                trainingRequest.getId(),
                trainingRequest.getCreatedAt()
        );
    }

    public String buildStatusUpdateMailBody(TrainingRequest trainingRequest) {
        return """
                A Caloryx rendszerben frissült az edzéskérelmed státusza.

                Új státusz: %s
                Edző: %s
                Heti edzésszám: %s
                Egy alkalom hossza: %s perc
                Preferált helyszín: %s
                Edzői megjegyzés: %s
                Kérés azonosító: %s
                Létrehozva: %s

                A részletekért lépj be az alkalmazásba.
                """.formatted(
                EmailFormatUtils.status(trainingRequest.getStatus()),
                EmailFormatUtils.safe(trainingRequest.getCoachProfile().getUser().getFullName()),
                trainingRequest.getWeeklyTrainingCount(),
                trainingRequest.getSessionDurationMinutes(),
                EmailFormatUtils.safe(trainingRequest.getPreferredLocation()),
                EmailFormatUtils.safe(trainingRequest.getDescription()),
                trainingRequest.getId(),
                trainingRequest.getCreatedAt()
        );
    }
}