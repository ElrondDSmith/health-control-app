package org.snp.telegraminputservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.snp.telegraminputservice.dto.ReportDtoRq;
import org.snp.telegraminputservice.exception.PdfClientException;
import org.snp.telegraminputservice.provider.UrlProvider;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfClient {

    private final RestTemplate restTemplate;
    private final UrlProvider urlProvider;

    public byte[] generateReport(ReportDtoRq reportDtoRq) {
        String url = urlProvider.getPdf();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_PDF));

        HttpEntity<ReportDtoRq> entity = new HttpEntity<>(reportDtoRq, httpHeaders);

        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    byte[].class
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("PdfClient: получен PDF ({} bytes) от {}", response.getBody().length, url);
                return response.getBody();
            } else {
                byte[] body = response.getBody();
                String bodyPreview = (body != null) ? ("[bytes:" + response.getBody().length + "]") : "empty";
                String message = String.format("PdfClient: неожиданный ответ от %s: %s, body=%s",
                        url, response.getStatusCode(), bodyPreview);
                log.error(message);
                throw new RuntimeException();
            }
        } catch (RestClientException ex) {
            log.error("PdfClient: ошибка при вызове {}: {}", url, ex.getMessage());
            throw new PdfClientException("Ошибка при запросе PDF: " + ex.getMessage(), ex);
        }
    }
}