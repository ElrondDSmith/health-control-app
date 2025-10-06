package org.snp.pdf_service.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Data
public class ReportDtoRq {

    private ReportUserDto user;
    private ReportPeriodDto period;
    private List<ReportRecordDto> records;

    @Data
    public static class ReportUserDto {
        private Long id;
        private String name;
    }

    @Data
    public static class ReportRecordDto {
        private OffsetDateTime timestamp;
        private Integer systolic;
        private Integer diastolic;
        private Integer pulse;
    }

    @Data
    public static class ReportPeriodDto {
        private LocalDate startDate;
        private LocalDate endDate;
    }
}


