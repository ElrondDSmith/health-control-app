package org.snp.telegraminputservice.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class TelegramWebhookRegistrar {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.webhook-path}")
    private String webhookPath;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public void registerWebhook() {
        String setWebhook = String.format("https://api.telegram.org/bot%s/setWebhook?url=%s/webhook", botToken, webhookPath);
        try {
            String response = restTemplate.getForObject(setWebhook, String.class);
            log.info("Webhook registration response: " + response);
        } catch (Exception e) {
            log.info("Failed to register webhook: " + e.getMessage());
        }
    }
}