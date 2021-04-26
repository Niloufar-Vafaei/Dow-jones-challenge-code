package com.rbc.imp.stockmarkets.exceptions;


public class ServiceException extends RuntimeException {
    public ServiceException(String messageError){
        super(messageError);
    }
}
