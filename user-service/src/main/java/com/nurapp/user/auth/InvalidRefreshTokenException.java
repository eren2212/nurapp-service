package com.nurapp.user.auth;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException() {
        super("Geçersiz veya süresi dolmuş refresh token");
    }
}
