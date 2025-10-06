package org.snp.telegraminputservice.handler;

import org.snp.telegraminputservice.common.RequestResult;
import org.snp.telegraminputservice.dto.PressureRecordDtoRs;
import org.snp.telegraminputservice.formatter.PressureMessageFormatter;
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
public class PressureMenuHandler extends AbstractMenuHandler {

    private final HealthDataClient healthDataClient;
    private final PressureMessageFormatter pressureMessageFormatter;

    public PressureMenuHandler(MessagesProperties messagesProperties,
                               HealthDataClient healthDataClient,
                               PressureMessageFormatter pressureMessageFormatter) {
        super(messagesProperties);
        this.healthDataClient = healthDataClient;
        this.pressureMessageFormatter = pressureMessageFormatter;
    }

    @Override
    protected UserState targetState() {
        return UserState.PRESSURE_MENU;
    }

    @Override
    protected List<String> commands() {
        return List.of("За дату", "За последние N дней", "Все записи", "Главное меню", "/help", "Помощь");
    }

    @Override
    public List<PartialBotApiMethod<?>> handle(Message message, UserSession userSession) {

        String text = message.getText();
        Long userId = message.getFrom().getId();
        SendMessage menuMessage = createMessage(message);
        SendMessage errorMessage = createMessage(message);
        SendMessage resultMessage = createMessage(message);

        switch (text) {
            case "За дату" -> {
                resultMessage.setText(messagesProperties.getPressureMenu().getOneDayRequest());
                resultMessage.setReplyMarkup(KeyboardFactory.goBackOrMainMenuKeyboard());
                userSession.setUserState(UserState.WAITING_FOR_DATE);
                return single(resultMessage);
            }
            case "За последние N дней" -> {
                resultMessage.setText(messagesProperties.getPressureMenu().getPeriodRequest());
                resultMessage.setReplyMarkup(KeyboardFactory.goBackOrMainMenuKeyboard());
                userSession.setUserState(UserState.WAITING_FOR_DAYS);
                return single(resultMessage);
            }
            case "Все записи" -> {
                RequestResult<List<PressureRecordDtoRs>> result = healthDataClient.getAllById(userId);
                if (!result.isSuccess()) {
                    errorMessage.setText(messagesProperties.getPressureMenu().getFailedToReceiveRecords()
                            + result.errorMessage());
                    goToMainMenu(userSession, menuMessage);
                    return multiple(errorMessage, menuMessage);
                }
                List<PressureRecordDtoRs> recordDtoRs = result.data();
                if (recordDtoRs == null || recordDtoRs.isEmpty()) {
                    errorMessage.setText(messagesProperties.getPressureMenu().getNoRecords());
                    goToMainMenu(userSession, menuMessage);
                    return multiple(errorMessage, menuMessage);
                } else {
                    resultMessage.setText(pressureMessageFormatter.formatListOfRecords(result.data()));
                    goToMainMenu(userSession, menuMessage);
                    return multiple(resultMessage, menuMessage);
                }
            }
            case "Главное меню" -> {
                goToMainMenu(userSession, menuMessage);
                return single(menuMessage);
            }

            case "/help", "Помощь" -> {
                resultMessage.setText(messagesProperties.getPressureMenu().getHelp());
                return single(resultMessage);
            }
            default -> {
                resultMessage.setText(messagesProperties.getPressureMenu().getUnknownRequest());
                return single(resultMessage);
            }
        }
    }
}
