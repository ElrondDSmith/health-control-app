package org.snp.telegraminputservice.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public record RequestResult<T>(T data, String errorMessage, HttpStatusCode status) {
    public static <T> RequestResult<T> success(T data) {
        return new RequestResult<>(data, null, HttpStatus.OK);
    }

    public static <T> RequestResult<T> failure(String errorMessage, HttpStatusCode status) {
        return new RequestResult<>(null, errorMessage, status);
    }

    public boolean isSuccess() {
        return data != null;
    }
}
