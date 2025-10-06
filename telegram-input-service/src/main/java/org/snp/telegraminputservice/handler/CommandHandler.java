package org.snp.telegraminputservice.handler;

import org.snp.telegraminputservice.model.UserSession;
import org.snp.telegraminputservice.model.UserState;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

public interface CommandHandler {

    boolean canHandle(UserState userState, Message message);

    List<PartialBotApiMethod<?>> handle(Message message, UserSession userSession);
}
