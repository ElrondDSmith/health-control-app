package org.snp.telegraminputservice.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UrlProvider {

    @Value("${other-service.base-url}")
    private String baseUrl;

    public String getUserRegUrl() {
        return baseUrl + "/save_user";
    }
    public String getBloodPressureSaveUrl() {
        return baseUrl + "/save";
    }
    public String getAllRecordsByIdUrl() {
        return baseUrl + "/user/{tgId}";
    }
    public String getRecordsByIdAndDate() {
        return baseUrl + "/user/{tgId}/date?date={date}";
    }
    public String getRecordsByIdAndLastNDays() {
        return baseUrl + "/user/{tgId}/number_of_days?numberOfDays={numberOfDays}";
    }
}

