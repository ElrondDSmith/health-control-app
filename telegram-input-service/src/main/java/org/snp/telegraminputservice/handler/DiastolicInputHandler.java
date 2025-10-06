package org.snp.telegraminputservice.handler;

import org.snp.telegraminputservice.messages.MessagesProperties;
import org.snp.telegraminputservice.model.UserSession;
import org.snp.telegraminputservice.model.UserState;
import org.snp.telegraminputservice.properties.PressureLimits;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
public class DiastolicInputHandler extends AbstractInputHandler {

    private final PressureLimits limits;

    public DiastolicInputHandler(MessagesProperties messagesProperties, PressureLimits pressureLimits) {
        super(messagesProperties);
        this.limits = pressureLimits;
    }

    @Override
    protected UserState targetState() {
        return UserState.WAITING_FOR_DIASTOLIC;
    }

    @Override
    public List<PartialBotApiMethod<?>> handle(Message message, UserSession userSession) {

        String text = message.getText();

        SendMessage menuMessage = createMessage(message);
        SendMessage errorMessage = createMessage(message);
        SendMessage resultMessage = createMessage(message);

        if ("Главное меню".equals(text)) {
            goToMainMenu(userSession, menuMessage);
            return single(menuMessage);
        } else {
            try {
                int diastolic = Integer.parseInt(text);
                if (diastolic < limits.getMinDia() || diastolic > limits.getMaxDia()) {
                    throw new NumberFormatException();
                }
                userSession.setDiastolic(diastolic);
                userSession.setUserState(UserState.WAITING_FOR_PULSE);
                resultMessage.setText(messagesProperties.getPressureInput().getPulseInput());
                return single(resultMessage);
            } catch (NumberFormatException e) {
                errorMessage.setText(messagesProperties.getPressureInput().getIncorrectDiastolicInput());
                return single(errorMessage);
            }
        }
    }
}
