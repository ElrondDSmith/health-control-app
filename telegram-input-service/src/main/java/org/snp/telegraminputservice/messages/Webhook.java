package org.snp.telegraminputservice.messages;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Webhook {

    private String setWebhook;
    private String webhookSetError;
    private String webhookUrl;
    private String webhookRegistrationResponse;
    private String failedRegisterWebhook;

}