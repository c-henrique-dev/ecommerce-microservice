package br.com.loomi.reportmicroservice.exceptions.handler;

import br.com.loomi.reportmicroservice.exceptions.BadRequestException;
import br.com.loomi.reportmicroservice.exceptions.ErrorResponse;
import br.com.loomi.reportmicroservice.exceptions.InvalidFieldException;
import br.com.loomi.reportmicroservice.exceptions.NotFoundException;
import br.com.loomi.reportmicroservice.models.dtos.FieldError;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(
            NotFoundException e) {
        return ErrorResponse.defaultResponse(e.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(
            BadRequestException e) {
        return ErrorResponse.defaultResponse(e.getMessage());
    }

    @ExceptionHandler({FeignException.FeignClientException.class, FeignException.FeignServerException.class, FeignException.class})
    public ErrorResponse handleFeignException(FeignException e) {
        try {
            String responseBody = e.contentUTF8();

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> errorDetails = objectMapper.readValue(responseBody, Map.class);

            String message = (String) errorDetails.getOrDefault("message", e.getMessage());

            HttpStatus httpStatus = HttpStatus.resolve(e.status());
            if (httpStatus == null) {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            }

            return new ErrorResponse(message, null);
        } catch (Exception ex) {
            return ErrorResponse.defaultResponse("Error processing Feign response: " + e.getMessage());
        }
    }

    @ExceptionHandler(InvalidFieldException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleCampoInvalidoException(InvalidFieldException e) {
        return new ErrorResponse(
                "Validation error",
                List.of(new FieldError(e.getCampo(), e.getMessage())));
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccesDeniedException(AccessDeniedException e) {
        return ErrorResponse.defaultResponse(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleErrosNaoTratados(RuntimeException e) {
        return new ErrorResponse(
                "An unexpected error has occurred. Please contact the administration"
                , List.of());
    }
}