package org.snp.telegraminputservice.messages;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class MainMenu {
    private String start;
    private String back;
    private String registrationFail;
    private String help;
    private String unknownRequest;
}