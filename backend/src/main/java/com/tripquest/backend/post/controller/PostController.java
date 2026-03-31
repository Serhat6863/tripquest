package com.tripquest.backend.post.controller;

import com.tripquest.backend.auth.entity.User;
import com.tripquest.backend.post.dto.CreatePostRequest;
import com.tripquest.backend.post.dto.PostResponse;
import com.tripquest.backend.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody CreatePostRequest request) {
        User currentUser = currentUser();
        return ResponseEntity.ok(postService.createPost(currentUser.getId(), request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostResponse>> getPostsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(postService.getPostsByUser(userId));
    }

    @GetMapping("/country/{countryCode}")
    public ResponseEntity<List<PostResponse>> getPostsByCountry(@PathVariable String countryCode) {
        return ResponseEntity.ok(postService.getPostsByCountry(countryCode));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        User currentUser = currentUser();
        postService.deletePost(postId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<PostResponse> likePost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.likePost(postId));
    }

    private User currentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
