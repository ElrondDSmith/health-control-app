package org.snp.telegraminputservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.snp.telegraminputservice.common.RequestResult;
import org.snp.telegraminputservice.dto.PressureRecordDtoRq;
import org.snp.telegraminputservice.dto.PressureRecordDtoRs;
import org.snp.telegraminputservice.dto.UserRegDto;
import org.snp.telegraminputservice.provider.UrlProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthDataClient {

    private final UrlProvider urlProvider;
    private final RestTemplate restTemplate;

    public RequestResult<List<PressureRecordDtoRs>> getByIdAndDate(Long tgId, LocalDate date) {
        Map<String, Object> urlVariables = Map.of("tgId", tgId, "date", date.toString());
        RequestResult<PressureRecordDtoRs[]> requestResult = safeGetForEntity(
                urlProvider.getRecordsByIdAndDate(),
                PressureRecordDtoRs[].class,
                urlVariables);
        return convertArrayResult(requestResult);
    }

    public RequestResult<List<PressureRecordDtoRs>> getByIdAndDays(Long tgId, int numberOfDays) {
        Map<String, Object> urlVariables = Map.of("tgId", tgId, "numberOfDays", numberOfDays);
        RequestResult<PressureRecordDtoRs[]> requestResult = safeGetForEntity(
                urlProvider.getRecordsByIdAndLastNDays(),
                PressureRecordDtoRs[].class,
                urlVariables);
        return convertArrayResult(requestResult);
    }

    public RequestResult<List<PressureRecordDtoRs>> getAllById(Long tgId) {
        Map<String, Object> urlVariables = Map.of("tgId", tgId);
        RequestResult<PressureRecordDtoRs[]> requestResult = safeGetForEntity(
                urlProvider.getAllRecordsByIdUrl(),
                PressureRecordDtoRs[].class,
                urlVariables);
        return convertArrayResult(requestResult);
    }

    public RequestResult<String> registerUser(UserRegDto dtoRs) {
        return safePostForEntity(urlProvider.getUserRegUrl(), dtoRs, String.class);
    }

    public RequestResult<String> savePressure(PressureRecordDtoRq dtoRq) {
        return safePostForEntity(urlProvider.getBloodPressureSaveUrl(), dtoRq, String.class);
    }

    private RequestResult<List<PressureRecordDtoRs>> convertArrayResult(RequestResult<PressureRecordDtoRs[]> arrayResult) {
        if (arrayResult.isSuccess()) {
            PressureRecordDtoRs[] rawArray = Optional.ofNullable(arrayResult.data()).orElse(new PressureRecordDtoRs[0]);
            return RequestResult.success(Arrays.asList(rawArray));
        } else {
            return RequestResult.failure(arrayResult.errorMessage(), arrayResult.status());
        }
    }

    private <T> RequestResult<T> safeGetForEntity(String url, Class<T> responseType, Map<String, ?> uriVariables) {
        try {
            ResponseEntity<T> response = restTemplate.getForEntity(url, responseType, uriVariables);

            if (response.getStatusCode().is2xxSuccessful()) {
                if (response.getBody() != null) {
                    log.info("Успешно: Запрос к {} вернул статус {}", url, response.getStatusCode());
                    return RequestResult.success(response.getBody());
                }else {
                    log.warn("Ошибка: Запрос к {} вернул статус {} с пустым телом", url, response.getStatusCode());
                    return RequestResult.failure("Пустой ответ от сервера", response.getStatusCode());
                }
            } else {
                log.warn("Ошибка: Запрос к {} вернул статус {}", url, response.getStatusCode());
                return RequestResult.failure("Невалидный ответ от сервера", response.getStatusCode());
            }
        } catch (HttpStatusCodeException e) {
            HttpStatus status = HttpStatus.resolve(e.getStatusCode().value());
            if (status == null) {
                log.error("Неизвестный HTTP статус {} при запросе к {}: {}", status, url, e.getResponseBodyAsString());
            } else {
                switch (status) {
                    case NOT_FOUND -> log.warn("404: ресурс не найден при запросе к {}", url);
                    case UNAUTHORIZED -> log.warn("401: неавторизованный доступ к {}", url);
                    case INTERNAL_SERVER_ERROR -> log.warn("500: внутренняя ошибка сервера {}", url);
                    default -> log.error("Ошибка {} при запросе к {}: {}", status, url, e.getResponseBodyAsString());
                }
            }
            return RequestResult.failure("Ошибка при запросе: " + status, status);
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при запросе к {}: {}", url, e.getMessage(), e);
            return RequestResult.failure("Ошибка соединения: " + e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    private <Req, Res> RequestResult<Res> safePostForEntity(String url, Req requestBody, Class<Res> responseType) {
        try {
            ResponseEntity<Res> response = restTemplate.postForEntity(url, requestBody, responseType);
            if(response.getStatusCode().is2xxSuccessful()) {
                log.info("Успешно: POST {}. Статус {}", url, response.getStatusCode());
                return RequestResult.success(response.getBody());
            } else {
                log.warn("Ошибка: POST {}. Статус {}", url, response.getStatusCode());
                return RequestResult.failure("Ошибка при POST", response.getStatusCode());
            }
        } catch (HttpStatusCodeException e) {
            HttpStatus status = HttpStatus.resolve(e.getStatusCode().value());
            log.error("Ошибка при POST запросе к {}: {}", url, e.getResponseBodyAsString());
            return RequestResult.failure("Ошибка POST: " + status, status);
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при POST запросе к {}: {}", url, e.getMessage());
            return RequestResult.failure("Ошибка соединения: " + e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
