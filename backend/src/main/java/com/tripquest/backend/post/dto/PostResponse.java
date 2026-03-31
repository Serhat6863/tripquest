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
public class PostResponse {

    private Long id;
    private FriendResponse author;
    private String countryCode;
    private String countryName;
    private String title;
    private String content;
    private String imageUrl;
    private Integer rating;
    private LocalDateTime createdAt;
    private Integer likesCount;
}
