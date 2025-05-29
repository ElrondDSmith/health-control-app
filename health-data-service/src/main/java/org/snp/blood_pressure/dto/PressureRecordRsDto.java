package org.snp.blood_pressure.dto;

import lombok.Data;

import java.time.LocalDateTime;


import java.time.LocalDateTime;
@Data
public class PressureRecordRsDto {

    private LocalDateTime timestamp;
    private Integer systolicPressure;
    private Integer diastolicPressure;
    private Integer pulse;
}
