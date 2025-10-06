package org.snp.telegraminputservice.formatter;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class FileNameFormatter {
    int count = 1;
    LocalDate lastDate = LocalDate.MIN;

    public String fileNameGenerator() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        if (lastDate.equals(LocalDate.now())) {
            count++;
        } else {
            count = 1;
            lastDate = LocalDate.now();
        }
        return "report-" + dateTimeFormatter.format(lastDate) + "-" + count + ".pdf";
    }
}
