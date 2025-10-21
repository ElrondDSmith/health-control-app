package org.snp.telegraminputservice.messages;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class PressureInput {

    private String systolicInput;
    private String incorrectSystolicInput;
    private String diastolicInput;
    private String incorrectDiastolicInput;
    private String pulseInput;
    private String incorrectPulseInput;
    private String dataSendConfirm;
    private String successDataSend;
    private String failedDataSend;
    private String unknownRequest;
}