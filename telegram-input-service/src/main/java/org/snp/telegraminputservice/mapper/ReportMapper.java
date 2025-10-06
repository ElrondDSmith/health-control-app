package org.snp.telegraminputservice.mapper;

import org.snp.telegraminputservice.dto.PressureRecordDtoRs;
import org.snp.telegraminputservice.dto.ReportDtoRq;
import org.snp.telegraminputservice.util.TelegramUserUtil;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Component
public class ReportMapper {

    public ReportDtoRq buildReportDtoRq(Message message, Long userId, List<PressureRecordDtoRs> recordsDtoRs, int days) {
        ReportDtoRq reportDtoRq = new ReportDtoRq();

        ReportDtoRq.ReportUserDto userDto = new ReportDtoRq.ReportUserDto();
        userDto.setId(userId);
        userDto.setName(TelegramUserUtil.resolveUserName(message));
        reportDtoRq.setUser(userDto);

        ReportDtoRq.ReportPeriodDto periodDto = new ReportDtoRq.ReportPeriodDto();
        periodDto.setStartDate(LocalDate.now().minusDays(days));
        periodDto.setEndDate(LocalDate.now());
        reportDtoRq.setPeriod(periodDto);

        List<ReportDtoRq.ReportRecordDto> reportRecords = recordsDtoRs.stream()
                .map(record -> mapToReportRecordDto(record))
                .toList();
        reportDtoRq.setRecords(reportRecords);
        return reportDtoRq;
    }

    private ReportDtoRq.ReportRecordDto mapToReportRecordDto(PressureRecordDtoRs record) {
        ReportDtoRq.ReportRecordDto recordDto = new ReportDtoRq.ReportRecordDto();
        recordDto.setTimestamp(record.getTimestamp().atOffset(ZoneOffset.UTC));
        recordDto.setSystolic(record.getSystolicPressure());
        recordDto.setDiastolic(record.getDiastolicPressure());
        recordDto.setPulse(record.getPulse());
        return recordDto;
    }
}
