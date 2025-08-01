package com.lms.requestDTO;

import lombok.Data;

@Data
public class UserRequestDTO {
    private String username;
    private String email;
    private String password;  // Send plain for create; hash in service!
    private String role;      // "STUDENT", "INSTRUCTOR", "ADMIN"
    private boolean enabled;  // default true
}
