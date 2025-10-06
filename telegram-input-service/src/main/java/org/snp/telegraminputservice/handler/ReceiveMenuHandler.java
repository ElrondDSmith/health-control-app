package org.snp.telegraminputservice.handler;

import org.snp.telegraminputservice.keyboard.KeyboardFactory;
import org.snp.telegraminputservice.messages.MessagesProperties;
import org.snp.telegraminputservice.model.UserSession;
import org.snp.telegraminputservice.model.UserState;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
public class ReceiveMenuHandler extends AbstractMenuHandler {

    public ReceiveMenuHandler(MessagesProperties messagesProperties) {
        super(messagesProperties);
    }

    @Override
    protected UserState targetState() {
        return UserState.RECEIVE_MENU;
    }

    @Override
    protected List<String> commands() {
        return List.of("Сообщение", "PDF", "Главное меню", "/help", "Помощь");
    }

    @Override
    public List<PartialBotApiMethod<?>> handle(Message message, UserSession userSession) {

        String text = message.getText();
        SendMessage menuMessage = createMessage(message);
        SendMessage errorMessage = createMessage(message);
        SendMessage resultMessage = createMessage(message);

        switch (text) {
            case "Сообщение" -> {
                userSession.setUserState(UserState.PRESSURE_MENU);
                resultMessage.setReplyMarkup(KeyboardFactory.pressureSubMenuKeyboard());
                resultMessage.setText(messagesProperties.getPressureMenu().getStart());
                return single(resultMessage);
            }
            case "PDF" -> {
                userSession.setUserState(UserState.PDF_MENU);
                resultMessage.setReplyMarkup(KeyboardFactory.pdfMenuKeyboard());
                resultMessage.setText(messagesProperties.getPdfMenu().getStart());
                return single(resultMessage);
            }
            case "Главное меню" -> {
                goToMainMenu(userSession, menuMessage);
                return single(menuMessage);
            }

            case "/help", "Помощь" -> {
                resultMessage.setText(messagesProperties.getReceiveMenu().getHelp());
                resultMessage.setReplyMarkup(KeyboardFactory.receiveMenuKeyboard());
                return single(resultMessage);
            }
            default -> {
                errorMessage.setText(messagesProperties.getReceiveMenu().getUnknownRequest());
                return single(errorMessage);
            }
        }
    }
}