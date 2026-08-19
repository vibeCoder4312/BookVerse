package com.bookverse.exception;

// Thrown when a logged-in user tries to modify something that isn't
// theirs - e.g. editing someone else's review. Different from
// InvalidCredentialsException (401 - "we don't know who you are") -
// this is 403 ("we know who you are, but you're not allowed to do this").
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
