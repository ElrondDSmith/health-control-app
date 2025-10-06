package org.snp.pdf_service.controller;

import lombok.RequiredArgsConstructor;
import org.snp.pdf_service.dto.ReportDtoRq;
import org.snp.pdf_service.service.PdfGeneratorService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping ("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final PdfGeneratorService pdfGeneratorService;

    @PostMapping(value = "/generate", produces = "application/pdf")
    public ResponseEntity<byte[]> generateReport(@RequestBody ReportDtoRq request) {
        byte[] pdf = pdfGeneratorService.generateReport(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
    @GetMapping(value = "test")
    public String testString() {
        return "test";
    }
}
