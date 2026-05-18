package com.example.shopflow.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
}