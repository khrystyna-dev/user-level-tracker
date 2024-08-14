package com.example.userleveltracker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;

/**
 * Data class representing user information related to a specific level and their result.
 */
@Getter
@Builder
@JsonPropertyOrder({"user_id", "level_id", "result"})
public class UserInfo {
    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("level_id")
    private Integer levelId;

    private Integer result;
}
