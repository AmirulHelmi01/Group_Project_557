package com.example.groupproject.model;

/**
 * POJO for error element sent by API
 */
public class ErrorResponse {
    // fail login
    private String status;
    private java.lang.Error error;
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public java.lang.Error getError() {
        return error;
    }
    public void setError(java.lang.Error error) {
        this.error = error;
    }
}
