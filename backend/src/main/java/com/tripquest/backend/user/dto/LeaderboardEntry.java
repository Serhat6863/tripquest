package com.tripquest.backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntry {

    private Integer rank;
    private Long userId;
    private String username;
    private String avatarUrl;
    private Integer travelScore;
    private Integer level;
    private Integer visitedCountriesCount;
    private Integer badgesCount;
}
