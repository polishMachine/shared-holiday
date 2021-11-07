package dev.machine.polish.helidon.sharedholiday.controllers;

public class ErrorResponse {
    private final String errorMsg; 

    public ErrorResponse(String message) {
        this.errorMsg = message;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
