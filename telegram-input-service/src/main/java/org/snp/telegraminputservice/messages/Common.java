package org.snp.telegraminputservice.messages;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class Common {

    private String unknownRequest;
    private String botInitializing;
    private String profile;

    private String selectActionOrStart;
    private String selectAction;
    private String failedToReceiveRecords;
}
