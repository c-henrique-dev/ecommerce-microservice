package br.com.loomi.authmicroservice.exceptions;

import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class ApiErrors {
    private HttpStatus status;
    private Integer statusCode;
    private List<String> errors;
    public ApiErrors(HttpStatus status, Integer statusCode, List<String> errors) {
        super();
        this.errors = errors;
        this.statusCode = statusCode;
        this.status = status;
    }
}