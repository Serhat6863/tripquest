package com.tripquest.backend.user.dto;

import com.tripquest.backend.user.entity.BadgeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeResponse {

    private Long id;
    private BadgeType type;
    private String label;
    private String description;
    private LocalDateTime unlockedAt;
}
