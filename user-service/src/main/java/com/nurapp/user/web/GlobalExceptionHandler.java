package com.nurapp.user.web;

import com.nurapp.user.auth.InvalidRefreshTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Servis genelinde tutarlı hata cevabı (RFC 7807 Problem Details).
 * Domain exception'ları burada ProblemDetail'e çevrilir; framework hataları (404/400 vb.)
 * spring.mvc.problemdetails.enabled ile aynı formatta döner.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ProblemDetail handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problem.setTitle("Geçersiz refresh token");
        return problem;
    }
}
