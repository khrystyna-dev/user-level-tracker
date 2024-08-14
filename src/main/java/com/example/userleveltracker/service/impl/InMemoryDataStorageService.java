package com.example.userleveltracker.service.impl;

import com.example.userleveltracker.exception.DataProcessingException;
import com.example.userleveltracker.model.UserInfo;
import com.example.userleveltracker.service.DataStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * In-memory implementation of the {@link DataStorageService} interface.
 * This service stores and retrieves user results and level information using in-memory data structures.
 * It is designed to handle concurrent access and ensures data consistency in a multithreaded environment.
 *
 * <p>The user results are stored in a {@code Map<Integer, Map<Integer, UserInfo>>}, where the outer map
 * key is the user ID, and the inner map key is the level ID. The results for each level are stored in a
 * {@code Map<Integer, Queue<UserInfo>>}, where the map key is the level ID.</p>
 *
 * <p>The class also includes logic to limit the number of results returned by the service, which is
 * configurable through the {@code api.user.top.results.limit} property.</p>
 */
@Service
public class InMemoryDataStorageService implements DataStorageService {
    private final Map<Integer, Map<Integer, UserInfo>> userResults = new ConcurrentHashMap<>();
    private final Map<Integer, Queue<UserInfo>> levelResults = new ConcurrentHashMap<>();
    @Value("${api.user.top.results.limit}")
    private int resultsLimit;

    /**
     * Stores or updates the result of a user for a specific level.
     * This method synchronizes access to ensure that updates to the user results are thread-safe.
     * It checks if a user's result for a level already exists, and updates it if the new result
     * is higher. The updated result is then added to the level's results queue.
     *
     * @param userId  the ID of the user
     * @param levelId the ID of the level
     * @param result  the result to be set for the user at the specified level
     */
    @Override
    public synchronized void setInfo(int userId, int levelId, int result) {
        UserInfo newUserInfo = UserInfo.builder().userId(userId).levelId(levelId).result(result).build();

        userResults.computeIfAbsent(userId, k -> new ConcurrentHashMap<>());
        Map<Integer, UserInfo> userLevels = userResults.get(userId);

        if (userLevels.containsKey(levelId)) {
            UserInfo existingUserInfo = userLevels.get(levelId);
            if (existingUserInfo.getResult() < result) {
                userLevels.put(levelId, newUserInfo);
            }
        } else {
            userLevels.put(levelId, newUserInfo);
        }

        levelResults.computeIfAbsent(levelId, k -> new ConcurrentLinkedQueue<>());
        Queue<UserInfo> levelQueue = levelResults.get(levelId);

        levelQueue.removeIf(info -> info.getUserId() == userId);
        levelQueue.add(newUserInfo);
    }

    /**
     * Retrieves the top results for a specific user across all levels.
     * This method sorts the user's results in descending order based on the result value and
     * level ID. If no results are found for the specified user, a {@link DataProcessingException}
     * is thrown.
     *
     * @param userId the ID of the user whose information is to be retrieved
     * @return a list of {@link UserInfo} objects representing the user's top results
     * @throws DataProcessingException if no results are found for the specified user ID
     */
    @Override
    public List<UserInfo> getUserInfo(int userId) {
        userResults.getOrDefault(userId, new HashMap<>()).values().stream()
                .findAny()
                .orElseThrow(() -> new DataProcessingException(
                        "Oops! There are no results for the specified user ID: " + userId));

        return userResults.get(userId).values().stream()
                .sorted(Comparator.comparingInt(UserInfo::getResult)
                        .thenComparingInt(UserInfo::getLevelId).reversed())
                .limit(resultsLimit)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the top users and their results for a specific level.
     * This method sorts the level's results in descending order based on the result value and
     * user ID. If no results are found for the specified level, a {@link DataProcessingException}
     * is thrown.
     *
     * @param levelId the ID of the level for which information is to be retrieved
     * @return a list of {@link UserInfo} objects representing the top users for the specified level
     * @throws DataProcessingException if no results are found for the specified level ID
     */
    @Override
    public List<UserInfo> getLevelInfo(int levelId) {
        levelResults.getOrDefault(levelId, new ConcurrentLinkedQueue<>()).stream()
                .findAny()
                .orElseThrow(() -> new DataProcessingException(
                        "Oops! There are no results for the specified level ID: " + levelId));

        return levelResults.get(levelId).stream()
                .sorted(Comparator.comparingInt(UserInfo::getResult)
                        .thenComparingInt(UserInfo::getUserId).reversed())
                .limit(resultsLimit)
                .collect(Collectors.toList());
    }
}
