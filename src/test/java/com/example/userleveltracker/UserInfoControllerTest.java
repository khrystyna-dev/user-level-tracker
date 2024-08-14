package com.example.userleveltracker;

import com.example.userleveltracker.model.UserInfoRq;
import com.example.userleveltracker.service.impl.InMemoryDataStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InMemoryDataStorageService dataStore;

    private UserInfoRq userInfoRq;

    @BeforeEach
    void setUp() {
        userInfoRq = UserInfoRq.builder().userId(1).levelId(1).result(55).build();

        dataStore.setInfo(1, 1, 55);
        dataStore.setInfo(1, 2, 8);
    }

    @Test
    @DisplayName("givenGetUserInfo_whenValidUserId_thenGetUserInfoList")
    public void testGetUserInfo_validUserId_ok() throws Exception {
        mockMvc.perform(get("/userinfo/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].user_id", is(1)))
                .andExpect(jsonPath("$[0].level_id", is(1)))
                .andExpect(jsonPath("$[0].result", is(55)))
                .andExpect(jsonPath("$[1].user_id", is(1)))
                .andExpect(jsonPath("$[1].level_id", is(2)))
                .andExpect(jsonPath("$[1].result", is(8)));
    }

    @Test
    @DisplayName("givenGetLevelInfo_whenValidLevelId_thenGetLevelInfoList")
    public void testGetLevelInfo_validLevelId_ok() throws Exception {
        mockMvc.perform(get("/levelinfo/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].user_id", is(1)))
                .andExpect(jsonPath("$[0].level_id", is(1)))
                .andExpect(jsonPath("$[0].result", is(55)));
    }

    @Test
    @DisplayName("givenSetInfo_whenValidInput_thenSuccess")
    public void testSetInfo_validInput_ok() throws Exception {
        mockMvc.perform(put("/setinfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInfoRq)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("givenSetInfo_whenInvalidInput_thenBadRequest")
    public void testSetInfo_invalidInput_notOk() throws Exception {
        UserInfoRq invalidUserInfoRq = UserInfoRq.builder()
                .userId(1)
                .levelId(-1)
                .result(55).build();

        mockMvc.perform(put("/setinfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUserInfoRq)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(
                        "[Level ID must be greater than or equal to 1]")));
    }

    @Test
    @DisplayName("givenGetLevelInfo_whenDataNotExist_thenGetException")
    public void testGetLevelInfo_dataNotExist_notOk() throws Exception {
        int invalidLevelId = 999;

        mockMvc.perform(get("/levelinfo/" + invalidLevelId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message",
                        is("Oops! There are no results for the specified level ID: " + invalidLevelId)));
    }

    @Test
    @DisplayName("givenGetUserInfo_whenDataNotExist_thenGetException")
    public void testGetUserInfo_dataNotExist_notOk() throws Exception {
        int invalidUserId = 999;

        mockMvc.perform(get("/userinfo/" + invalidUserId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message",
                        is("Oops! There are no results for the specified user ID: " + invalidUserId)));
    }

    @Test
    @DisplayName("givenGetUserInfo_whenMultithreadedAccess_thenSuccess")
    public void testGetUserInfo_multithreaded_ok() {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CompletableFuture<Void>[] futures = new CompletableFuture[10];

        for (int i = 0; i < 10; i++) {
            futures[i] = CompletableFuture.runAsync(() -> {
                try {
                    mockMvc.perform(get("/userinfo/1"))
                            .andExpect(status().isOk())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$", hasSize(2)))
                            .andExpect(jsonPath("$[0].user_id", is(1)))
                            .andExpect(jsonPath("$[0].level_id", is(1)))
                            .andExpect(jsonPath("$[0].result", is(55)))
                            .andExpect(jsonPath("$[1].user_id", is(1)))
                            .andExpect(jsonPath("$[1].level_id", is(2)))
                            .andExpect(jsonPath("$[1].result", is(8)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, executor);
        }

        CompletableFuture.allOf(futures).join();
        executor.shutdown();
    }

    @Test
    @DisplayName("givenSetInfo_whenMultithreadedAccess_thenSuccess")
    public void testSetInfo_multithreaded_ok() {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CompletableFuture<Void>[] futures = new CompletableFuture[10];

        for (int i = 0; i < 10; i++) {
            futures[i] = CompletableFuture.runAsync(() -> {
                try {
                    UserInfoRq request = UserInfoRq.builder().userId(1).levelId(1).result(55).build();
                    mockMvc.perform(put("/setinfo")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                            .andExpect(status().isOk());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, executor);
        }

        CompletableFuture.allOf(futures).join();
        executor.shutdown();
    }
}
