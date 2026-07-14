package com.nurapp.user.web;

import com.nurapp.user.auth.EmailAlreadyInUseException;
import com.nurapp.user.auth.InvalidCredentialsException;
import com.nurapp.user.auth.InvalidRefreshTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Servis genelinde tutarlı hata cevabı (RFC 7807 Problem Details).
 * Domain exception'ları burada ProblemDetail'e çevrilir; framework hataları (404/400/validation)
 * spring.mvc.problemdetails.enabled ile aynı formatta döner.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ProblemDetail handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
        return problem(HttpStatus.UNAUTHORIZED, "Geçersiz refresh token", ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ProblemDetail handleInvalidCredentials(InvalidCredentialsException ex) {
        return problem(HttpStatus.UNAUTHORIZED, "Giriş başarısız", ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ProblemDetail handleEmailInUse(EmailAlreadyInUseException ex) {
        return problem(HttpStatus.CONFLICT, "E-posta kullanımda", ex.getMessage());
    }

    private ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        return pd;
    }
}
