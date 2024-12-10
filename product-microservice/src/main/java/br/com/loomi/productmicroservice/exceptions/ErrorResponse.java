package br.com.loomi.productmicroservice.exceptions;

import br.com.loomi.productmicroservice.models.dtos.FieldError;

import java.util.List;

public record ErrorResponse(String message, List<FieldError> errors) {

    public static ErrorResponse defaultResponse(String message){
        return new ErrorResponse(message, List.of());
    }

    public static ErrorResponse conflict(String message){
        return new ErrorResponse(message, List.of());
    }
}