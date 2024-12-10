package br.com.loomi.ordermicroservice.exceptions;

import br.com.loomi.ordermicroservice.models.dtos.FieldError;

import java.util.List;

public record ErrorResponse(String message, List<FieldError> erros) {

    public static ErrorResponse defaultResponse(String message){
        return new ErrorResponse(message, List.of());
    }

    public static ErrorResponse conflict(String message){
        return new ErrorResponse(message, List.of());
    }

}