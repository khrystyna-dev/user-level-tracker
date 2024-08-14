package com.example.userleveltracker.service;

import com.example.userleveltracker.model.UserInfo;

import java.util.List;

/**
 * Interface for managing and retrieving user data in a storage system.
 * This interface defines the contract for storing user results and retrieving user and level information.
 */
public interface DataStorageService {

    /**
     * Stores or updates the result of a user for a specific level.
     *
     * @param userId  the ID of the user
     * @param levelId the ID of the level
     * @param result  the result to be set for the user at the specified level
     */
    void setInfo(int userId, int levelId, int result);

    /**
     * Retrieves the top results for a specific user across all levels.
     *
     * @param userId the ID of the user whose information is to be retrieved
     * @return a list of {@link UserInfo} objects representing the user's results across all levels
     */
    List<UserInfo> getUserInfo(int userId);

    /**
     * Retrieves the top users and their results for a specific level.
     *
     * @param levelId the ID of the level for which information is to be retrieved
     * @return a list of {@link UserInfo} objects representing the top users for the specified level
     */
    List<UserInfo> getLevelInfo(int levelId);
}
