package org.snp.telegraminputservice.messages;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Log {

    private String serviceNotResponding;
    private String unsupportedMethod;
    private String sendingMessageError;
    private String sessionState;
}
