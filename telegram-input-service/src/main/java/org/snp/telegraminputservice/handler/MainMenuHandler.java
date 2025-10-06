package org.snp.telegraminputservice.handler;

import org.snp.telegraminputservice.common.RequestResult;
import org.snp.telegraminputservice.dto.UserRegDto;
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
public class MainMenuHandler extends AbstractMenuHandler {

    private final HealthDataClient healthDataClient;

    public MainMenuHandler(MessagesProperties messagesProperties, HealthDataClient healthDataClient) {
        super(messagesProperties);
        this.healthDataClient = healthDataClient;
    }

    @Override
    protected UserState targetState() {
        return UserState.NONE;
    }

    @Override
    protected List<String> commands() {
        return List.of(
                "/start",
                "Старт",
                "/help",
                "Помощь",
                "Сохранить данные о давлении",
                "Получить данные о давлении");
    }

    @Override
    public List<PartialBotApiMethod<?>> handle(Message message, UserSession userSession) {

        String text = message.getText();
        Long userId = message.getFrom().getId();
        String userName = message.getFrom().getUserName();

        SendMessage sendMessage = createMessage(message);

        switch (text) {
            case "/start", "Старт" -> {
                sendMessage.setReplyMarkup(KeyboardFactory.mainMenuKeyboard());
                userSession.setUserState(UserState.NONE);
                UserRegDto userRegDto = new UserRegDto(userId, userName);
                RequestResult<String> result = healthDataClient.registerUser(userRegDto);
                if (result.isSuccess()) {
                    sendMessage.setText(messagesProperties.getMainMenu().getStart());
                } else {
                    sendMessage.setText(messagesProperties.getMainMenu().getRegistrationFail());
                }
            }
            case "/help", "Помощь" -> {
                sendMessage.setText(messagesProperties.getMainMenu().getHelp());
                sendMessage.setReplyMarkup(KeyboardFactory.mainMenuKeyboard());
            }
            case "Сохранить данные о давлении" -> {
                userSession.setUserState(UserState.WAITING_FOR_SYSTOLIC);
                sendMessage.setReplyMarkup(KeyboardFactory.goToMainMenuKeyboard());
                userSession.setUserId(userId);
                userSession.setUserName(userName);
                sendMessage.setText(messagesProperties.getPressureInput().getSystolicInput());
            }
            case "Получить данные о давлении" -> goToReceiveMenu(userSession, sendMessage);

            default -> {
                sendMessage.setText(messagesProperties.getMainMenu().getUnknownRequest());
                sendMessage.setReplyMarkup(KeyboardFactory.mainMenuKeyboard());
            }
        }
        return single(sendMessage);
    }
}
