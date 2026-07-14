package com.nurapp.user.auth;

public class EmailAlreadyInUseException extends RuntimeException {
    public EmailAlreadyInUseException() {
        super("Bu e-posta zaten kullanımda");
    }
}
