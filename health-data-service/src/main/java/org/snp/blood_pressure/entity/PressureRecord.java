package org.snp.blood_pressure.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PressureRecord {

    public PressureRecord() {
    }

    public PressureRecord(Integer appUserId, Integer systolicPressure, Integer diastolicPressure, Integer pulse) {
        this.appUserId = appUserId;
        this.systolicPressure = systolicPressure;
        this.diastolicPressure = diastolicPressure;
        this.pulse = pulse;
    }

    private Integer id;
    private LocalDateTime dateTime;
    private Integer appUserId;
    private Integer systolicPressure;
    private Integer diastolicPressure;
    private Integer pulse;
}
