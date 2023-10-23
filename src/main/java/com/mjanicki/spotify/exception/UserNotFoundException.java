package com.mjanicki.spotify.exception;

public class UserNotFoundException extends RuntimeException {
    
    public UserNotFoundException(String email) {
        super("Couldn't find a user with email " + email);
    }
}
