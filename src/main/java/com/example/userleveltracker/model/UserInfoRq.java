package com.example.userleveltracker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Request data class representing user information to be submitted for a specific level and result.
 */
@Getter
@Setter
@Builder
public class UserInfoRq {
    @NotNull(message = "User ID cannot be null")
    @Min(value = 1, message = "User ID must be greater than or equal to 1")
    @JsonProperty("user_id")
    private Integer userId;

    @NotNull(message = "Level ID cannot be null")
    @Min(value = 1, message = "Level ID must be greater than or equal to 1")
    @JsonProperty("level_id")
    private Integer levelId;

    @NotNull(message = "Result cannot be null")
    @Min(value = 0, message = "Result must be greater than or equal to 0")
    private Integer result;
}
