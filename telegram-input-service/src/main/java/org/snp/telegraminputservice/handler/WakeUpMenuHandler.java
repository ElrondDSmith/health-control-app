package org.snp.telegraminputservice.handler;

import org.snp.telegraminputservice.messages.MessagesProperties;
import org.snp.telegraminputservice.model.UserSession;
import org.snp.telegraminputservice.model.UserState;
import org.snp.telegraminputservice.service.SleepService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
@Profile("sleep")
public class WakeUpMenuHandler extends AbstractCommandHandler {

    private final SleepService sleepService;

    public WakeUpMenuHandler(MessagesProperties messagesProperties,
                             SleepService sleepService) {
        super(messagesProperties);
        this.sleepService = sleepService;
    }

    @Override
    protected UserState targetState() {
        return UserState.WAKE_UP_MENU;
    }

    @Override
    protected List<String> commands() {
        return List.of("РАЗБУДИТЬ СЕРВИСЫ");
    }

    @Override
    protected boolean allowAnyText() {
        return true;
    }

    @Override
    public List<PartialBotApiMethod<?>> handle(Message message, UserSession userSession) {

        String text = message.getText();
        SendMessage menuMessage = createMessage(message);
        SendMessage errorMessage = createMessage(message);

        if ("РАЗБУДИТЬ СЕРВИСЫ".equals(text)) {
            if (sleepService.pingAllServices()) {
                goToMainMenu(userSession, menuMessage);
                return single(menuMessage);
            } else {
                errorMessage.setText(messagesProperties.getWakeUpMenu().getFailure());
                return single(errorMessage);
            }
        } else {
            goToWakeUpMenu(userSession, errorMessage);
            return single(errorMessage);
        }
    }

    public List<PartialBotApiMethod<?>> sleep(Message message, UserSession userSession) {
        SendMessage menuMessage = createMessage(message);
        goToWakeUpMenu(userSession, menuMessage);
        return single(menuMessage);
    }
}
