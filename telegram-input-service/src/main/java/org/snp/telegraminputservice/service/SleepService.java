package org.snp.telegraminputservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.snp.telegraminputservice.messages.MessagesProperties;
import org.snp.telegraminputservice.model.UserSession;
import org.snp.telegraminputservice.properties.SleepProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@Profile("sleep")
@RequiredArgsConstructor
public class SleepService {

    private final SleepProperties sleepProperties;
    private final RestTemplate restTemplate;
    private final MessagesProperties messages;

    public boolean isInactive(UserSession userSession) {
        Instant lastActivity = userSession.getLastActivity();
        return lastActivity == null || Duration.between(lastActivity, Instant.now())
                .toMinutes() >= sleepProperties.getTimeoutMinutes();
    }

    public boolean pingAllServices() {

        List<String> sleepingServices = receiveSleepingServices();
        return sleepingServices.isEmpty();
    }

    private List<String> receiveSleepingServices() {

        return sleepProperties.getPingUrls().stream()
                .filter(url -> !pingService(url))
                .toList();
    }

    private boolean pingService(String url) {

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.warn(messages.getLog().getServiceNotResponding(), url, e.getMessage());
            return false;
        }
    }
}
