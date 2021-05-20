package com.rbc.imp.stockmarkets.exceptions;


import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

public class GlobalException extends RuntimeException {
    int code;
    public GlobalException(Throwable throwable) {
        super(throwable.getMessage());
        if( throwable.getCause() instanceof DataIntegrityViolationException)
        {
            if(throwable.getCause().getCause() instanceof ConstraintViolationException)
              {
                code=1;
              }
        }
    }
}
