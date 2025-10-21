package org.snp.telegraminputservice.handler;

import lombok.RequiredArgsConstructor;
import org.snp.telegraminputservice.dto.PressureRecordDtoRq;
import org.snp.telegraminputservice.keyboard.KeyboardFactory;
import org.snp.telegraminputservice.messages.MessagesProperties;
import org.snp.telegraminputservice.model.UserSession;
import org.snp.telegraminputservice.model.UserState;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public abstract class AbstractCommandHandler implements CommandHandler {

    protected final MessagesProperties messagesProperties;

    protected abstract UserState targetState();

    protected abstract List<String> commands();

    protected abstract boolean allowAnyText();

    public boolean canHandle(UserState userState, Message message) {
        if (userState != targetState() || message == null || message.getText() == null) {
            return false;
        }
        if (allowAnyText()) {
            return true;
        }
        return commands().contains(message.getText().trim());
    }

    protected List<PartialBotApiMethod<?>> single(PartialBotApiMethod<?> method) {
        return List.of(method);
    }

    protected List<PartialBotApiMethod<?>> multiple(PartialBotApiMethod<?>... methods) {
        return List.of(methods);
    }

    protected SendMessage createMessage(Message message) {
        return new SendMessage(String.valueOf(message.getChatId()), "");
    }

    private void goToMenu(UserSession userSession, UserState targetSession,
                          SendMessage sendMessage, ReplyKeyboardMarkup targetKeyboard, String returnMessage) {
        userSession.setUserState(targetSession);
        sendMessage.setReplyMarkup(targetKeyboard);
        sendMessage.setText(returnMessage);
    }

    protected void goToMainMenu(UserSession userSession, SendMessage sendMessage) {
        goToMenu(userSession,
                UserState.NONE,
                sendMessage,
                KeyboardFactory.mainMenuKeyboard(),
                messagesProperties.getMainMenu().getBack());
    }

    protected void goToReceiveMenu(UserSession userSession, SendMessage sendMessage) {
        goToMenu(userSession,
                UserState.RECEIVE_MENU,
                sendMessage,
                KeyboardFactory.receiveMenuKeyboard(),
                messagesProperties.getReceiveMenu().getStart());
    }

    protected void goToPressureMenu(UserSession userSession, SendMessage sendMessage) {
        goToMenu(userSession,
                UserState.PRESSURE_MENU,
                sendMessage,
                KeyboardFactory.pressureSubMenuKeyboard(),
                messagesProperties.getPressureMenu().getStart());
    }

    protected void goToWaitingForSystolic(UserSession userSession, SendMessage sendMessage,
                                          Long userId, String userName) {
        goToMenu(userSession,
                UserState.WAITING_FOR_SYSTOLIC,
                sendMessage,
                KeyboardFactory.goToMainMenuKeyboard(),
                messagesProperties.getPressureInput().getSystolicInput());

        userSession.setUserId(userId);
        userSession.setUserName(userName);
    }

    protected void goToWakeUpMenu(UserSession userSession, SendMessage sendMessage) {
        goToMenu(userSession,
                UserState.WAKE_UP_MENU,
                sendMessage,
                KeyboardFactory.wakeUpKeyboard(),
                messagesProperties.getWakeUpMenu().getSleep());
    }

    protected PressureRecordDtoRq pressureDtoCreate(UserSession session) {
        PressureRecordDtoRq dto = new PressureRecordDtoRq();
        dto.setTgId(session.getUserId());
        dto.setUserName(session.getUserName());
        dto.setSystolicPressure(session.getSystolic());
        dto.setDiastolicPressure(session.getDiastolic());
        dto.setPulse(session.getPulse());
        dto.setTimestamp(LocalDateTime.now().toString());
        return dto;
    }
}
