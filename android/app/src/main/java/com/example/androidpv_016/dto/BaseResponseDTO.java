package com.example.androidpv_016.dto;

public class BaseResponseDTO<T>{
    private String message;
    private boolean isSuccess;
    private T payload;

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public T getPayload() {
        return payload;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }
}
