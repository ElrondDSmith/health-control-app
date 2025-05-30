package org.snp.telegraminputservice.configuration;

import org.snp.telegraminputservice.formatter.PressureMessageFormatter;
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
    private final PressureMessageFormatter pressureMessageFormatter;
    private final UrlProvider urlProvider;

    @Autowired
    public TelegramBotConfig(TelegramBotProperties telegramBotProperties,
                             PressureMessageFormatter pressureMessageFormatter,
                             UrlProvider urlProvider) {
        this.telegramBotProperties = telegramBotProperties;
        this.pressureMessageFormatter = pressureMessageFormatter;
        this.urlProvider = urlProvider;
    }

    @Bean
    public TelegramBotService telegramBotService(RestTemplate restTemplate) {
        SetWebhook setWebhook = SetWebhook.builder().url(telegramBotProperties.getWebhookPath()).build();
        TelegramBotService telegramBotService = new TelegramBotService(telegramBotProperties,
                restTemplate,
                pressureMessageFormatter,
                urlProvider);
        try {
            telegramBotService.setWebhook(setWebhook);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Ошибка при установке Webhook", e);
        }

        return telegramBotService;
    }
}
