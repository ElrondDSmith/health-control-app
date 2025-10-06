package org.snp.telegraminputservice.handler;

import org.snp.telegraminputservice.common.RequestResult;
import org.snp.telegraminputservice.dto.PressureRecordDtoRs;
import org.snp.telegraminputservice.formatter.PressureMessageFormatter;
import org.snp.telegraminputservice.messages.MessagesProperties;
import org.snp.telegraminputservice.model.UserSession;
import org.snp.telegraminputservice.model.UserState;
import org.snp.telegraminputservice.service.HealthDataClient;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
public class DaysInputHandler extends AbstractInputHandler {

    private final HealthDataClient healthDataClient;
    private final PressureMessageFormatter pressureMessageFormatter;

    public DaysInputHandler(MessagesProperties messagesProperties,
                            HealthDataClient healthDataClient,
                            PressureMessageFormatter pressureMessageFormatter) {
        super(messagesProperties);
        this.healthDataClient = healthDataClient;
        this.pressureMessageFormatter = pressureMessageFormatter;
    }

    @Override
    protected UserState targetState() {
        return UserState.WAITING_FOR_DAYS;
    }

    @Override
    public List<PartialBotApiMethod<?>> handle(Message message, UserSession userSession) {

        String text = message.getText();
        Long userId = message.getFrom().getId();

        SendMessage menuMessage = createMessage(message);
        SendMessage errorMessage = createMessage(message);
        SendMessage resultMessage = createMessage(message);

        if ("Главное меню".equals(text)) {
            goToMainMenu(userSession, menuMessage);
            return single(menuMessage);
        }
        if ("Назад".equals(text)) {
            goToPressureMenu(userSession, menuMessage);
            return single(menuMessage);
        } else {
            try {
                int days = Integer.parseInt(text.trim());
                RequestResult<List<PressureRecordDtoRs>> result = healthDataClient.getByIdAndDays(userId, days);
                if (!result.isSuccess()) {
                    errorMessage.setText(messagesProperties.getPressureMenu().getFailedToReceiveRecords()
                            + result.errorMessage());
                    goToMainMenu(userSession, menuMessage);
                    return multiple(errorMessage, menuMessage);
                }
                List<PressureRecordDtoRs> recordDtoRs = result.data();
                if (recordDtoRs == null || recordDtoRs.isEmpty()) {
                    errorMessage.setText(messagesProperties.getPressureMenu().getNoRecordsByDays());
                    goToMainMenu(userSession, menuMessage);
                    return multiple(errorMessage, menuMessage);
                } else {
                    resultMessage.setText(pressureMessageFormatter.formatListOfRecords(result.data()));
                    goToMainMenu(userSession, menuMessage);
                    return multiple(resultMessage, menuMessage);
                }
            } catch (NumberFormatException e) {
                errorMessage.setText(messagesProperties.getPressureMenu().getIncorrectDaysFormat());
                return single(errorMessage);
            }
        }
    }
}
