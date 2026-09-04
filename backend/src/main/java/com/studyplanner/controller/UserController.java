package com.studyplanner.controller;

import com.studyplanner.dto.UpdateProfileRequest;
import com.studyplanner.dto.UserProfileResponse;
import com.studyplanner.security.UserPrincipal;
import com.studyplanner.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserProfileResponse profile = userService.getProfile(userPrincipal.getId());
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request,
                                                             @AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserProfileResponse updated = userService.updateProfile(userPrincipal.getId(), request);
        return ResponseEntity.ok(updated);
    }
}
