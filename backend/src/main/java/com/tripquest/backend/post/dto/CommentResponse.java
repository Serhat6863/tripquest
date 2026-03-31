package com.tripquest.backend.post.dto;

import com.tripquest.backend.friends.dto.FriendResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private Long id;
    private FriendResponse author;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
