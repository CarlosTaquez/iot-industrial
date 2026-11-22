package com.example.iot.actiondispatcher.dto.response;

public record ApiErrorResponse(String status, String message) {
    public static ApiErrorResponse error(String message) {
        return new ApiErrorResponse("ERROR", message);
    }
}
