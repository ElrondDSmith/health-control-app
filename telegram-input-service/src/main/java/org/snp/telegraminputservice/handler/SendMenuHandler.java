package org.snp.telegraminputservice.handler;

import org.snp.telegraminputservice.common.RequestResult;
import org.snp.telegraminputservice.keyboard.KeyboardFactory;
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
public class SendMenuHandler extends AbstractMenuHandler {

    private final HealthDataClient healthDataClient;

    public SendMenuHandler(MessagesProperties messagesProperties, HealthDataClient healthDataClient) {
        super(messagesProperties);
        this.healthDataClient = healthDataClient;
    }

    @Override
    protected UserState targetState() {
        return UserState.WAITING_FOR_SEND;
    }

    @Override
    protected List<String> commands() {
        return List.of("СОХРАНИТЬ", "Исправить данные", "Главное меню");
    }

    @Override
    public List<PartialBotApiMethod<?>> handle(Message message, UserSession userSession) {

        String text = message.getText();
        Long userId = message.getFrom().getId();
        String userName = message.getFrom().getUserName();

        SendMessage menuMessage = createMessage(message);
        SendMessage errorMessage = createMessage(message);
        SendMessage resultMessage = createMessage(message);

        switch (text) {
            case "СОХРАНИТЬ" -> {
                goToMainMenu(userSession, menuMessage);
                RequestResult<String> result = healthDataClient.savePressure(pressureDtoCreate(userSession));
                if (result.isSuccess()) {
                    resultMessage.setText(messagesProperties.getPressureInput().getSuccessDataSend());
                } else {
                    resultMessage.setText(messagesProperties.getPressureInput().getFailedDataSend()
                            + result.errorMessage());
                }
                return multiple(resultMessage, menuMessage);
            }
            case "Исправить данные" -> {
                userSession.setUserState(UserState.WAITING_FOR_SYSTOLIC);
                resultMessage.setReplyMarkup(KeyboardFactory.goBackOrMainMenuKeyboard());
                userSession.setUserId(userId);
                userSession.setUserName(userName);
                resultMessage.setText(messagesProperties.getPressureInput().getSystolicInput());
                return single(resultMessage);
            }
            case "Главное меню" -> {
                goToMainMenu(userSession, menuMessage);
                return single(menuMessage);
            }
            default -> {
                errorMessage.setText(messagesProperties.getPressureInput().getUnknownRequest());
                return single(errorMessage);
            }
        }
    }
}
