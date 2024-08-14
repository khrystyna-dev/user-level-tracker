package com.example.userleveltracker.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * A data class representing details of an error.
 */
@Data
@AllArgsConstructor
public class ErrorDetails {
    private Date timestamp;
    private String message;
    private String details;
}