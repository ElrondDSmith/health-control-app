package org.snp.telegraminputservice.dto;

import lombok.Data;

@Data
public class PressureRecordDtoRq {
    private Long tgId;
    private String userName;
    private Integer systolicPressure;
    private Integer diastolicPressure;
    private Integer pulse;
    private String timestamp;
}
