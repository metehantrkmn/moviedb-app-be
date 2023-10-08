package com.metehan.authentication.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
public class AuthenticationException {
    private String status;
    private String message;
    private Throwable throwable;
    private LocalDateTime date;
}
