package com.ecommerce.project.security.jwt.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {

    @NotBlank
    @Size(min=3,max=20)
    private String username;

    @NotBlank
    @Email
    @Size(max=50)
    private String email;

    private Set<String> role;

    @NotBlank
    @Size(min=6,max=20,message = "Password should be min 6 and max 20 chars")
    private String password;
}
