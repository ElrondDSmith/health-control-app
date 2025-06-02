package org.snp.telegraminputservice.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.snp.telegraminputservice.common.RequestResult;
import org.snp.telegraminputservice.configuration.TelegramBotProperties;
import org.snp.telegraminputservice.dto.PressureRecordDtoRq;
import org.snp.telegraminputservice.dto.PressureRecordDtoRs;
import org.snp.telegraminputservice.dto.UserRegDto;
import org.snp.telegraminputservice.formatter.PressureMessageFormatter;
import org.snp.telegraminputservice.handler.TelegramCommandHandler;
import org.snp.telegraminputservice.model.UserSession;
import org.snp.telegraminputservice.model.UserState;
import org.snp.telegraminputservice.provider.UrlProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.WebhookBot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Slf4j
public class TelegramBotService implements WebhookBot {

    private final TelegramBotProperties telegramBotProperties;
    private final TelegramCommandHandler telegramCommandHandler;

    private final Map<Long, UserSession> sessions = new HashMap<>();

    @Autowired
    public TelegramBotService(TelegramBotProperties telegramBotProperties,
                              TelegramCommandHandler telegramCommandHandler) {
        this.telegramBotProperties = telegramBotProperties;
        this.telegramCommandHandler = telegramCommandHandler;
    }

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

            return switch (session.getUserState()) {
                case NONE -> telegramCommandHandler.handleMainMenuCommand(message, session);
                case WAITING_FOR_SYSTOLIC -> telegramCommandHandler.handleSystolicInput(message, session);
                case WAITING_FOR_DIASTOLIC -> telegramCommandHandler.handleDiastolicInput(message, session);
                case WAITING_FOR_PULSE -> telegramCommandHandler.handlePulseInput(message, session);
                case WAITING_FOR_SEND -> telegramCommandHandler.handleSendInput(message, session);
                case PRESSURE_MENU -> telegramCommandHandler.handlePressureMenuCommand(message, session);
                case WAITING_FOR_DATE -> telegramCommandHandler.handleDateInput(message, session);
                case WAITING_FOR_DAYS -> telegramCommandHandler.handleDaysInput(message, session);
            };
        }
        return null;
    }

    @Override
    public void setWebhook(SetWebhook setWebhook) throws TelegramApiException {
        System.out.println("Webhook установлен: " + setWebhook.getUrl());
    }
}