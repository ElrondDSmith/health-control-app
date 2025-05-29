package org.snp.telegraminputservice.formatter;

import org.snp.telegraminputservice.dto.PressureRecordDtoRs;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PressureMessageFormatter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public String formatRecord(PressureRecordDtoRs record) {
        String formatedDate = (record.getTimestamp() != null ? record.getTimestamp().format(DATE_TIME_FORMATTER) : "неизвестна");
        return String.format("📅 Дата: %s\n🔼 Верхнее давление: %d\n🔽 Нижнее давление: %d\n❤️ Пульс: %d",
               formatedDate, record.getSystolicPressure(), record.getDiastolicPressure(), record.getPulse());
    }

    public String formatListOfRecords(List<PressureRecordDtoRs> records) {
        if (records.isEmpty()) {
            return "По запросу данных о давлении не найдено";
        }
        return records.stream().map(this::formatRecord).collect(Collectors.joining("\n\n"));
    }
}
