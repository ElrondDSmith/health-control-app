package org.snp.telegraminputservice.configuration;

import org.snp.telegraminputservice.formatter.PressureMessageFormatter;
import org.snp.telegraminputservice.handler.TelegramCommandHandler;
import org.snp.telegraminputservice.service.HealthDataClient;
import org.snp.telegraminputservice.service.TelegramBotService;
import org.snp.telegraminputservice.provider.UrlProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Configuration
public class TelegramBotConfig {
    private final TelegramBotProperties telegramBotProperties;
    private final TelegramCommandHandler telegramCommandHandler;

    @Autowired
    public TelegramBotConfig(TelegramBotProperties telegramBotProperties,
                             TelegramCommandHandler telegramCommandHandler) {
        this.telegramBotProperties = telegramBotProperties;
        this.telegramCommandHandler = telegramCommandHandler;
    }

    @Bean
    public TelegramBotService telegramBotService(RestTemplate restTemplate) {
        SetWebhook setWebhook = SetWebhook.builder().url(telegramBotProperties.getWebhookPath()).build();
        TelegramBotService telegramBotService = new TelegramBotService(
                telegramBotProperties,
                telegramCommandHandler);
        try {
            telegramBotService.setWebhook(setWebhook);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Ошибка при установке Webhook", e);
        }

        return telegramBotService;
    }
}