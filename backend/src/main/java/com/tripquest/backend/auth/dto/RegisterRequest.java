package com.tripquest.backend.auth.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterRequest {

    private String username;
    private String email;
    private String password;
}
