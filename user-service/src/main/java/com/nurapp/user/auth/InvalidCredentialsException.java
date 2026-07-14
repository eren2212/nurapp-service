package com.nurapp.user.auth;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("E-posta veya şifre hatalı");
    }
}
