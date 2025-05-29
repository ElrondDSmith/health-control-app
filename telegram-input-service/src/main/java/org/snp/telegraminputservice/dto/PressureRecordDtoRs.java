package org.snp.telegraminputservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PressureRecordDtoRs {

    private LocalDateTime timestamp;
    private Integer systolicPressure;
    private Integer diastolicPressure;
    private Integer pulse;
}
