package org.snp.telegraminputservice.messages;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class WakeUpMenu {

    private String sleep;
    private String failure;
}
