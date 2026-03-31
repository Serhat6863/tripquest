package com.tripquest.backend.user.controller;

import com.tripquest.backend.auth.entity.User;
import com.tripquest.backend.user.dto.BucketlistRequest;
import com.tripquest.backend.user.dto.BucketlistResponse;
import com.tripquest.backend.user.dto.UpdateProfileRequest;
import com.tripquest.backend.user.dto.UserProfileResponse;
import com.tripquest.backend.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateProfile(@RequestBody UpdateProfileRequest request) {
        User currentUser = currentUser();
        return ResponseEntity.ok(userService.updateProfile(currentUser.getId(), request));
    }

    @PostMapping("/me/countries")
    public ResponseEntity<UserProfileResponse> addVisitedCountry(@RequestBody Map<String, String> body) {
        User currentUser = currentUser();
        String countryCode = body.get("countryCode");
        // TODO: Validate countryCode format (ISO 3166-1 alpha-2)
        return ResponseEntity.ok(userService.addVisitedCountry(currentUser.getId(), countryCode));
    }

    @GetMapping("/me/bucketlist")
    public ResponseEntity<BucketlistResponse> getMyBucketlist() {
        User currentUser = currentUser();
        return ResponseEntity.ok(userService.getBucketlist(currentUser.getId()));
    }

    @GetMapping("/{userId}/bucketlist")
    public ResponseEntity<BucketlistResponse> getUserBucketlist(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getBucketlist(userId));
    }

    @PostMapping("/me/bucketlist")
    public ResponseEntity<BucketlistResponse> addToBucketlist(@Valid @RequestBody BucketlistRequest request) {
        User currentUser = currentUser();
        return ResponseEntity.ok(userService.addToBucketlist(currentUser.getId(), request));
    }

    @DeleteMapping("/me/bucketlist/{countryCode}")
    public ResponseEntity<Void> removeFromBucketlist(@PathVariable String countryCode) {
        User currentUser = currentUser();
        userService.removeFromBucketlist(currentUser.getId(), countryCode);
        return ResponseEntity.noContent().build();
    }

    private User currentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
