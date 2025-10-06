package org.snp.telegraminputservice.messages;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "messages")
@Data
public class MessagesProperties {

    private MainMenu mainMenu;
    private PressureInput pressureInput;
    private ReceiveMenu receiveMenu;
    private PressureMenu pressureMenu;
    private PdfMenu pdfMenu;
    private Common common;
}
