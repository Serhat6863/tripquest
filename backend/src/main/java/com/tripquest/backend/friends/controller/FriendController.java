package com.tripquest.backend.friends.controller;

import com.tripquest.backend.auth.entity.User;
import com.tripquest.backend.friends.dto.FriendResponse;
import com.tripquest.backend.friends.dto.FriendshipResponse;
import com.tripquest.backend.friends.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @PostMapping("/request/{receiverId}")
    public ResponseEntity<FriendshipResponse> sendRequest(@PathVariable Long receiverId) {
        User currentUser = currentUser();
        return ResponseEntity.ok(friendService.sendRequest(currentUser.getId(), receiverId));
    }

    @PutMapping("/accept/{friendshipId}")
    public ResponseEntity<FriendshipResponse> acceptRequest(@PathVariable Long friendshipId) {
        User currentUser = currentUser();
        return ResponseEntity.ok(friendService.acceptRequest(friendshipId, currentUser.getId()));
    }

    @PutMapping("/decline/{friendshipId}")
    public ResponseEntity<FriendshipResponse> declineRequest(@PathVariable Long friendshipId) {
        User currentUser = currentUser();
        return ResponseEntity.ok(friendService.declineRequest(friendshipId, currentUser.getId()));
    }

    @GetMapping
    public ResponseEntity<List<FriendResponse>> getFriends() {
        User currentUser = currentUser();
        return ResponseEntity.ok(friendService.getFriends(currentUser.getId()));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<FriendshipResponse>> getPendingRequests() {
        User currentUser = currentUser();
        return ResponseEntity.ok(friendService.getPendingRequests(currentUser.getId()));
    }

    private User currentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
