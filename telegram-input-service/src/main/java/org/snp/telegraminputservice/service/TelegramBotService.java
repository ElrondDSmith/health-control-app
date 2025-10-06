package org.snp.telegraminputservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.snp.telegraminputservice.configuration.TelegramBotProperties;
import org.snp.telegraminputservice.handler.CommandHandler;
import org.snp.telegraminputservice.messages.MessagesProperties;
import org.snp.telegraminputservice.model.UserSession;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class TelegramBotService extends TelegramWebhookBot {

    private final TelegramBotProperties telegramBotProperties;
    private final List<CommandHandler> handlers;
    private final MessagesProperties messagesProperties;

    private final Map<Long, UserSession> sessions = new HashMap<>();

    @PostConstruct
    public void init() {
        System.out.println("Bot is initializing...");
    }

    @Override
    public String getBotUsername() {
        return telegramBotProperties.getUsername();
    }

    @Override
    public String getBotToken() {
        return telegramBotProperties.getToken();
    }

    @Override
    public String getBotPath() {
        return telegramBotProperties.getWebhookPath();
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            UserSession session = sessions.computeIfAbsent(chatId, id -> new UserSession());
            List<PartialBotApiMethod<?>> results = null;

            for (CommandHandler handler : handlers) {
                if (handler.canHandle(session.getUserState(), message)) {
                    results = handler.handle(message, session);
                    break;
                }
            }
            if (results == null) {
                results = List.of(new SendMessage(
                        String.valueOf(chatId),
                        messagesProperties.getCommon().getUnknownRequest()));
            }
            for (PartialBotApiMethod<?> method : results) {
                try {
                    if (method instanceof BotApiMethod<?> botApiMethod) {
                        execute(botApiMethod);
                    } else if (method instanceof SendDocument sendDocument) {
                        execute(sendDocument);
                    } else if (method instanceof SendPhoto sendPhoto) {
                        execute(sendPhoto);
                    } else if (method instanceof SendVideo sendVideo) {
                        execute(sendVideo);
                    } else {
                        log.error("Неподдерживаемый метод: {}", method.getClass().getSimpleName());
                    }
                } catch (TelegramApiException e) {
                    log.error("Ошибка отправки сообщения: {}", e.getMessage(), e);
                }
            }
        }
        return null;
    }

    @Override
    public void setWebhook(SetWebhook setWebhook) throws TelegramApiException {
        System.out.println("Webhook установлен: " + setWebhook.getUrl());
    }
}