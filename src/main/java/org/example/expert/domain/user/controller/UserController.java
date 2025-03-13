package org.example.expert.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.ImageResponse;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PutMapping("/users")
    public void changePassword(@AuthenticationPrincipal AuthUser authUser, @RequestBody UserChangePasswordRequest userChangePasswordRequest) {
        userService.changePassword(authUser.getId(), userChangePasswordRequest);
    }

    @PostMapping("/users/profile-image")
    public ResponseEntity<ImageResponse> saveUserProfileImage(@AuthenticationPrincipal AuthUser authUser,
                                                            @RequestPart MultipartFile profileImage) {
        ImageResponse response = userService.saveUserProfileImage(authUser, profileImage);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users/profile-image")
    public ResponseEntity<Void> deleteUserProfileImage(@AuthenticationPrincipal AuthUser authUser){
        userService.deleteUserProfileImage(authUser);

        return ResponseEntity.ok().build();
    }
}
