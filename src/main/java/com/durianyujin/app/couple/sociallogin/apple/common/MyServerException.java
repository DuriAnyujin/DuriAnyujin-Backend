package com.durianyujin.app.couple.sociallogin.apple.common;

public class MyServerException extends RuntimeException {

    int status;

    String message;

    public MyServerException(int status, String message) {
        super(message);
        this.status = status;
    }
}
