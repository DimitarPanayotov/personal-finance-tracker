package com.dimitar.financetracker.dto.response.error;

import lombok.Data;

import java.util.Map;

import static com.dimitar.financetracker.util.HttpStatuses.BAD_REQUEST_STATUS_CODE;

@Data
public class ValidationErrorResponse extends ErrorResponse {
    private Map<String, String> errors;

    public ValidationErrorResponse(String message, String path, Map<String, String> errors) {
        super(BAD_REQUEST_STATUS_CODE, "Validation Failed", message, path);
        this.errors = errors;
    }
}