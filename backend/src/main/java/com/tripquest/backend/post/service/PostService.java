package com.tripquest.backend.post.service;

import com.tripquest.backend.auth.entity.User;
import com.tripquest.backend.auth.repository.UserRepository;
import com.tripquest.backend.friends.dto.FriendResponse;
import com.tripquest.backend.post.dto.CreatePostRequest;
import com.tripquest.backend.post.dto.PostResponse;
import com.tripquest.backend.post.entity.Post;
import com.tripquest.backend.post.repository.PostRepository;
import com.tripquest.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public PostResponse createPost(Long authorId, CreatePostRequest request) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("User not found: " + authorId));

        Post post = Post.builder()
                .author(author)
                .countryCode(request.getCountryCode())
                .countryName(request.getCountryName())
                .title(request.getTitle())
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .rating(request.getRating())
                .build();

        Post saved = postRepository.save(post);
        userService.addVisitedCountry(authorId, request.getCountryCode());

        return toResponse(saved);
    }

    public List<PostResponse> getPostsByUser(Long userId) {
        return postRepository.findByAuthorIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<PostResponse> getPostsByCountry(String countryCode) {
        return postRepository.findByCountryCodeOrderByCreatedAtDesc(countryCode).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deletePost(Long postId, Long currentUserId) {
        Post post = findPostOrThrow(postId);

        if (!post.getAuthor().getId().equals(currentUserId)) {
            throw new RuntimeException("Unauthorized: you can only delete your own posts");
        }

        postRepository.delete(post);
    }

    @Transactional
    public PostResponse likePost(Long postId) {
        Post post = findPostOrThrow(postId);
        post.setLikesCount(post.getLikesCount() + 1);
        return toResponse(postRepository.save(post));
    }

    private Post findPostOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found: " + postId));
    }

    private PostResponse toResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .author(toFriendResponse(post.getAuthor()))
                .countryCode(post.getCountryCode())
                .countryName(post.getCountryName())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .rating(post.getRating())
                .createdAt(post.getCreatedAt())
                .likesCount(post.getLikesCount())
                .build();
    }

    private FriendResponse toFriendResponse(User user) {
        return FriendResponse.builder()
                .id(user.getId())
                .username(user.getDisplayUsername())
                .avatarUrl(user.getAvatarUrl())
                .travelScore(user.getTravelScore())
                .level(user.getLevel())
                .build();
    }
}
