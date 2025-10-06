package org.snp.telegraminputservice.messages;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class PdfMenu {
    private String start;
    private String unknownRequest;
    private String failedToReceiveRecords;
    private String noRecordsForPeriod;
}