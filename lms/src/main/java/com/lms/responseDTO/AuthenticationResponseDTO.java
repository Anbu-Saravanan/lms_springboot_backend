package com.lms.responseDTO;

import com.lms.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationResponseDTO {
    private String token;
    private String refreshToken;
    private String email;
    private User.Role role;
}
