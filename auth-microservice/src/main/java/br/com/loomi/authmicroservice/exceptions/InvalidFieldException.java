package br.com.loomi.authmicroservice.exceptions;

import lombok.Getter;

public class InvalidFieldException extends RuntimeException {

    @Getter
    private String campo;

    public InvalidFieldException(String campo, String mensagem){
        super(mensagem);
        this.campo = campo;
    }
}
