package com.d121toastmaster.bot.ToastTalker.Exception;

import java.util.Map;

public class CustomException extends  RuntimeException{
    private String code;
    private String message;
    private Map<String, String> errors;

    public CustomException() {
        super();
    }
    public CustomException(String code, String message) {
        super();
        this.code = code;
        this.message = message;
    }
    public CustomException(Map<String, String> errors) {
        super();
        this.message = errors.toString();
        this.errors = errors;
    }
}
