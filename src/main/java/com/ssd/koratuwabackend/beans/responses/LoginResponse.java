package com.ssd.koratuwabackend.beans.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    private Integer id;
    private String name;
    private String email;
    private String address;
    private String phone;
    private String password;
    private String role;
    private String loginTime;
    private String token;
}
