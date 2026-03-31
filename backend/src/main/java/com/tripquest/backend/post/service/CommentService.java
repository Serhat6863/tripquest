package com.tripquest.backend.post.service;

import com.tripquest.backend.auth.entity.User;
import com.tripquest.backend.auth.repository.UserRepository;
import com.tripquest.backend.friends.dto.FriendResponse;
import com.tripquest.backend.post.dto.CommentResponse;
import com.tripquest.backend.post.dto.CreateCommentRequest;
import com.tripquest.backend.post.entity.Comment;
import com.tripquest.backend.post.entity.Post;
import com.tripquest.backend.post.repository.CommentRepository;
import com.tripquest.backend.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse addComment(Long postId, Long authorId, CreateCommentRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found: " + postId));
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("User not found: " + authorId));

        Comment comment = Comment.builder()
                .post(post)
                .author(author)
                .content(request.getContent())
                .build();

        return toResponse(commentRepository.save(comment));
    }

    public List<CommentResponse> getComments(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deleteComment(Long commentId, Long currentUserId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found: " + commentId));

        if (!comment.getAuthor().getId().equals(currentUserId)) {
            throw new RuntimeException("Unauthorized: you can only delete your own comments");
        }

        commentRepository.delete(comment);
    }

    public Long getCommentsCount(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    public CommentResponse toResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .author(toFriendResponse(comment.getAuthor()))
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
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
