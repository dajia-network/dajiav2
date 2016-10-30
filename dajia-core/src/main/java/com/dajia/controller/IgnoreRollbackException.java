package com.dajia.controller;

/**
 * Created by huhaonan on 2016/10/29.
 */
public class IgnoreRollbackException extends RuntimeException {

    private String message;

    private Throwable ex;

    public IgnoreRollbackException(String message, Throwable ex) {
        this.message = message;
        this.ex = ex;
    }

    public IgnoreRollbackException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Throwable getEx() {
        return ex;
    }

    public void setEx(Throwable ex) {
        this.ex = ex;
    }
}
