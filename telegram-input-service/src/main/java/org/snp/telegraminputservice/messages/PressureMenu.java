package org.snp.telegraminputservice.messages;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class PressureMenu {

    private String start;
    private String oneDayRequest;
    private String periodRequest;
    private String failedToReceiveRecords;
    private String noRecords;
    private String noRecordsByDate;
    private String noRecordsByDays;
    private String incorrectDateFormat;
    private String incorrectDaysFormat;
    private String help;
    private String unknownRequest;
}