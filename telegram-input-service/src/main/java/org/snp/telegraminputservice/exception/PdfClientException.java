package org.snp.telegraminputservice.exception;

public class PdfClientException extends RuntimeException{
    public PdfClientException(String message) {
        super(message);
    }

    public PdfClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
