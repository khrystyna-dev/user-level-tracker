package com.example.userleveltracker.exception;

/**
 * Exception thrown to indicate a problem during data processing.
 */
public class DataProcessingException extends RuntimeException {
    /**
     * Constructs a new DataProcessingException with the specified detail message.
     *
     * @param message The error message.
     */
    public DataProcessingException(String message) {
        super(message);
    }
}
