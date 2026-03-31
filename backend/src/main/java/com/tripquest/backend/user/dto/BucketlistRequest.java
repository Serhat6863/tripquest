package com.tripquest.backend.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BucketlistRequest {

    @NotBlank
    private String countryCode;

    @NotBlank
    private String countryName;
}
