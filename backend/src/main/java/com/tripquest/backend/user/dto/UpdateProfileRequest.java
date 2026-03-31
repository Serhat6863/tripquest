package com.tripquest.backend.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateProfileRequest {

    private String username;
    private String bio;
    private String avatarUrl;
}
