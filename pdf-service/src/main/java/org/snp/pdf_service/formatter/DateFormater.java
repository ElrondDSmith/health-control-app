package org.snp.pdf_service.formatter;


import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormater {

    public String periodDateFormater(LocalDate startDate, LocalDate endDate) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return "Period: " + dateTimeFormatter.format(startDate) + " - " + dateTimeFormatter.format(endDate);
    }

    public String singleDateFormater(OffsetDateTime timestamp) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return dateTimeFormatter.format(timestamp);
    }
}
