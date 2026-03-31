package com.tripquest.backend.friends.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendResponse {

    private Long id;
    private String username;
    private String avatarUrl;
    private Integer travelScore;
    private Integer level;
}
