package org.snp.telegraminputservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.snp.telegraminputservice.messages.MessagesProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
@RequiredArgsConstructor
public class TelegramWebhookRegistrar {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.webhook-path}")
    private String webhookPath;

    private final MessagesProperties messages;
    private final RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public void registerWebhook() {
        String setWebhook = String.format(messages.getWebhook().getWebhookUrl(), botToken, webhookPath);
        try {
            String response = restTemplate.getForObject(setWebhook, String.class);
            log.info(messages.getWebhook().getWebhookRegistrationResponse(), response);
        } catch (Exception e) {
            log.info(messages.getWebhook().getFailedRegisterWebhook(), e.getMessage());
        }
    }
}