package com.example.userleveltracker.controller;

import com.example.userleveltracker.model.UserInfo;
import com.example.userleveltracker.model.UserInfoRq;
import com.example.userleveltracker.service.impl.InMemoryDataStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller class for handling user information and level results.
 * This controller provides endpoints to retrieve and update user information and results for specific levels.
 */
@RestController
@RequiredArgsConstructor
public class UserInfoController {
    private final InMemoryDataStorageService dataStore;

    @GetMapping("/userinfo/{userId}")
    public ResponseEntity<List<UserInfo>> getUserInfo(@PathVariable int userId) {
        return ResponseEntity.ok(dataStore.getUserInfo(userId));
    }

    @GetMapping("/levelinfo/{levelId}")
    public ResponseEntity<List<UserInfo>> getLevelInfo(@PathVariable int levelId) {
        return ResponseEntity.ok(dataStore.getLevelInfo(levelId));
    }

    @PutMapping("/setinfo")
    public ResponseEntity<Void> setInfo(@RequestBody @Valid UserInfoRq userInfo) {
        dataStore.setInfo(userInfo.getUserId(), userInfo.getLevelId(), userInfo.getResult());
        return ResponseEntity.ok().build();
    }
}
