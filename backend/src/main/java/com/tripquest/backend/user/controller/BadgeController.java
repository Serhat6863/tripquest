package com.tripquest.backend.user.controller;

import com.tripquest.backend.auth.entity.User;
import com.tripquest.backend.user.dto.BadgeResponse;
import com.tripquest.backend.user.service.BadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeService badgeService;

    @GetMapping("/{userId}/badges")
    public ResponseEntity<List<BadgeResponse>> getUserBadges(@PathVariable Long userId) {
        return ResponseEntity.ok(badgeService.getUserBadges(userId));
    }

    @GetMapping("/me/badges")
    public ResponseEntity<List<BadgeResponse>> getMyBadges() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(badgeService.getUserBadges(currentUser.getId()));
    }
}
