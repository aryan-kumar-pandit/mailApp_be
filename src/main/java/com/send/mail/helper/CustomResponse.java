package com.send.mail.helper;

import org.springframework.http.HttpStatus;

public class CustomResponse {

    private String message;
    private HttpStatus httpStatus;
    private boolean success=false;

    public CustomResponse(HttpStatus httpStatus, String message, boolean success) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.success = success;
    }

    public CustomResponse() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
