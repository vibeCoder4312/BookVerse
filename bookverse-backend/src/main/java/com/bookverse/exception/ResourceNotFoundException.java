package com.bookverse.exception;

// A plain custom exception - it carries no special behavior, it just
// exists so we can throw something more meaningful than a generic
// RuntimeException, and so GlobalExceptionHandler can catch this
// SPECIFIC type and map it to a 404 response.
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
