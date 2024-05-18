package com.ssd.koratuwabackend.common.exceptions;

public class KoratuwaAppExceptions extends Exception {
    public KoratuwaAppExceptions(){
        super();
    }

    public KoratuwaAppExceptions(Exception ex){
        super(ex);
    }

    public KoratuwaAppExceptions(String message){
        super(message);
    }

    public KoratuwaAppExceptions(String message, Exception ex){
        super(message, ex);
    }
}
