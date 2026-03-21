package com.ratingservice.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ValidationErrorResponse(
        ErrorCode errorCode,
        String message,
        int status,
        String path,
        LocalDateTime timestamp,
        Map<String, String> errors
) {
}
