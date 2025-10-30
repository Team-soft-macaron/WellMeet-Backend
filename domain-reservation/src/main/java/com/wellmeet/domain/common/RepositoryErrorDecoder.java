package com.wellmeet.domain.common;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

public class RepositoryErrorDecoder {

    private static final String UNIQUE_RESERVATION_ERROR_MESSAGE = "unique_member_restaurant_available_date";

    private RepositoryErrorDecoder() {
    }

    public static boolean isUniqueConstraintViolation(DataIntegrityViolationException exception) {
        Throwable cause = exception.getCause();
        while (cause != null) {
            if (cause instanceof ConstraintViolationException cve) {
                return UNIQUE_RESERVATION_ERROR_MESSAGE.equals(cve.getConstraintName());
            }
            cause = cause.getCause();
        }
        return false;
    }
}
