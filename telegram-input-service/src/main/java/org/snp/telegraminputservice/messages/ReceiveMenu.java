package org.snp.telegraminputservice.messages;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ReceiveMenu {
    private String start;
    private String help;
    private String unknownRequest;
}