package com.firstclub.firstclub_membership.Exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
    public ResourceNotFoundException(String entity, String field, Object value) {
        super(entity + " not found with " + field + " = " + value);
    }
}
