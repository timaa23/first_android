package com.example.androidpv_016.dto.account;

public class ValidationLoginErrorDTO {
    private int status;
    private String title;
    private LoginErrorDTO errors;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LoginErrorDTO getErrors() {
        return errors;
    }

    public void setErrors(LoginErrorDTO errors) {
        this.errors = errors;
    }
}
