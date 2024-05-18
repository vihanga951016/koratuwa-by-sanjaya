package com.ssd.koratuwabackend.common.exceptions;

public class AuthorizationException extends KoratuwaAppExceptions {
    public AuthorizationException(String message, Exception ex) {
        super(message, ex);
    }

    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(Exception ex) {
        super(ex);
    }
}
