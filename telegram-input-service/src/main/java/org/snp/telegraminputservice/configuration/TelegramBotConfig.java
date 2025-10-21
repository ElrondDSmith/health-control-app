package org.snp.telegraminputservice.configuration;

import org.snp.telegraminputservice.handler.CommandHandler;
import org.snp.telegraminputservice.messages.MessagesProperties;
import org.snp.telegraminputservice.service.TelegramBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Configuration
public class TelegramBotConfig {
    private final TelegramBotProperties telegramBotProperties;
    private final List<CommandHandler> handlers;
    private final MessagesProperties messagesProperties;

    @Autowired
    public TelegramBotConfig(TelegramBotProperties telegramBotProperties,
                             List<CommandHandler> handlers,
                             MessagesProperties messagesProperties) {
        this.telegramBotProperties = telegramBotProperties;
        this.handlers = handlers;
        this.messagesProperties = messagesProperties;
    }

    @Bean
    public TelegramBotService telegramBotService(RestTemplate restTemplate) {
        SetWebhook setWebhook = SetWebhook.builder().url(telegramBotProperties.getWebhookPath()).build();
        TelegramBotService telegramBotService = new TelegramBotService(
                telegramBotProperties,
                handlers,
                messagesProperties);
        try {
            telegramBotService.setWebhook(setWebhook);
        } catch (TelegramApiException e) {
            throw new RuntimeException(messagesProperties.getWebhook().getWebhookSetError(), e);
        }
        return telegramBotService;
    }
}