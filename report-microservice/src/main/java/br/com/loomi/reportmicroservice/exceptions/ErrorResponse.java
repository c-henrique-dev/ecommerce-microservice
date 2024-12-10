package br.com.loomi.reportmicroservice.exceptions;

import br.com.loomi.reportmicroservice.models.dtos.FieldError;

import java.util.List;

public record ErrorResponse(String message, List<FieldError> errors) {

    public static ErrorResponse defaultResponse(String message){
        return new ErrorResponse(message, List.of());
    }

    public static ErrorResponse conflict(String message){
        return new ErrorResponse(message, List.of());
    }
}