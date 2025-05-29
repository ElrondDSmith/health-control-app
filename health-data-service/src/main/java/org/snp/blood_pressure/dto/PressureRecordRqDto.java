package org.snp.blood_pressure.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
public class PressureRecordRqDto {


    //@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    @NonNull
    private Long tgId;
    @NonNull
    private Integer systolicPressure;
    @NonNull
    private Integer diastolicPressure;
    @NonNull
    private Integer pulse;
}
