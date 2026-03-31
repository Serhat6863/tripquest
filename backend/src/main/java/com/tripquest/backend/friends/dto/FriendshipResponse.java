package com.tripquest.backend.friends.dto;

import com.tripquest.backend.friends.entity.FriendshipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipResponse {

    private Long id;
    private FriendResponse sender;
    private FriendResponse receiver;
    private FriendshipStatus status;
    private LocalDateTime createdAt;
}
