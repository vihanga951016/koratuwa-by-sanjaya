package com.ssd.koratuwabackend.beans.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetRequest {

    private Integer id;
    private String oldPassword;
    private String newPassword;
}
