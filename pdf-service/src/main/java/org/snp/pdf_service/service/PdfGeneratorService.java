package org.snp.pdf_service.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.snp.pdf_service.dto.ReportDtoRq;
import org.snp.pdf_service.formatter.DateFormater;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfGeneratorService {
    public byte[] generateReport(ReportDtoRq request) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             PDDocument document = new PDDocument()) {

            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
            contentStream.newLineAtOffset(100, 750);
            //contentStream.showText("Отчет о давлении: " + request.getUser().getName());
            contentStream.showText("Blood pressure report");
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Name: " + request.getUser().getName());
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText(new DateFormater()
                            .periodDateFormater(request.getPeriod().getStartDate(), request.getPeriod().getEndDate()));
            // TODO добавить период запроса
            contentStream.endText();

            //TODO нарисовать таблицу и график
            //drawTableHeader(contentStream, 50, 700);
            drawTable(document, contentStream, 50, 700, request);

            document.save(out);

            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании PDF", e);
        }
    }

    private void drawTableHeader(PDPageContentStream contentStream,
                                 float startX, float startY) throws IOException {

        String[] headers = {"Date", "Sys", "Dia", "Pulse"};

        float[] columnWidths = {150f, 100f, 100f, 100f};
        float nextX = startX;

        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        for (int i = 0; i < headers.length; i++) {
            contentStream.beginText();
            contentStream.newLineAtOffset(nextX + 5, startY - 15);
            contentStream.showText(headers[i]);
            contentStream.endText();
            nextX += columnWidths[i];
        }
    }

    private void drawTable(PDDocument document, PDPageContentStream contentStream,
                           float startX, float startY,
                           ReportDtoRq request) throws IOException {
        float rowHeight = 20f;
        float tableWidth = 500f;
        float[] columnWidths = {150f, 100f, 100f, 100f};
        //String[] headers = {"Дата", "Систолическое", "Диастолическое", "Пульс"};
        String[] headers = {"Date", "Sys", "Dia", "Pulse"};

        float pageBottomY = 50f;
        float nextX = startX;

        drawTableHeader(contentStream, startX, startY);
//        contentStream.setFont(PDType1Font.HELVETICA_BOLD,12);
//        for (int i = 0; i < headers.length; i++) {
//            contentStream.beginText();
//            contentStream.newLineAtOffset(nextX + 5, startY - 15);
//            contentStream.showText(headers[i]);
//            contentStream.endText();
//            nextX +=columnWidths[i];
//        }

        float nextY = startY - rowHeight;
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        for (var record : request.getRecords()) {
            if (nextY - rowHeight < pageBottomY) {
                contentStream.close();

                PDPage newPage = new PDPage();
                document.addPage(newPage);
                contentStream = new PDPageContentStream(document, newPage);
                nextY = startY;
                drawTableHeader(contentStream, startX, startY);
                nextY -= rowHeight;
                contentStream.setFont(PDType1Font.HELVETICA, 12);
            }
            nextX = startX;
            String[] values = {
                    String.valueOf(new DateFormater().singleDateFormater(record.getTimestamp())),
                    String.valueOf(record.getSystolic()),
                    String.valueOf(record.getDiastolic()),
                    String.valueOf(record.getPulse())
            };
            for (int i = 0; i < values.length; i++) {
                contentStream.beginText();
                contentStream.newLineAtOffset(nextX + 5, nextY - 15);
                contentStream.showText(values[i]);
                contentStream.endText();
                nextX += columnWidths[i];
            }
            nextY -= rowHeight;
        }
        contentStream.close();
    }
}
