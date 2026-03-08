package com.ratingservice.exception.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        ErrorCode errorCode,
        String message,
        int status,
        String path,
        LocalDateTime timestamp
){}

