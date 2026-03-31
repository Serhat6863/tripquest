package com.tripquest.backend.user.controller;

import com.tripquest.backend.auth.entity.User;
import com.tripquest.backend.user.dto.UpdateProfileRequest;
import com.tripquest.backend.user.dto.UserProfileResponse;
import com.tripquest.backend.user.service.UserService;
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

    // Public — no authentication required
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // Protected — updates the authenticated user's own profile
    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateProfile(@RequestBody UpdateProfileRequest request) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(userService.updateProfile(currentUser.getId(), request));
    }

    // Protected — adds a visited country to the authenticated user
    @PostMapping("/me/countries")
    public ResponseEntity<UserProfileResponse> addVisitedCountry(@RequestBody Map<String, String> body) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String countryCode = body.get("countryCode");
        // TODO: Validate countryCode format (ISO 3166-1 alpha-2)
        return ResponseEntity.ok(userService.addVisitedCountry(currentUser.getId(), countryCode));
    }
}
