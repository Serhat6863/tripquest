package com.tripquest.backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private Long id;
    private String username;
    private String bio;
    private String avatarUrl;
    private Integer travelScore;
    private Integer level;
    private LocalDateTime createdAt;
    private List<String> visitedCountries;
}
