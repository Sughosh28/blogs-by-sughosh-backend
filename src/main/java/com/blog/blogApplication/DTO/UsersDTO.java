package com.blog.blogApplication.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UsersDTO {
    private Long id;
    private String fullName;
    private String username;
    private String password;
    private String email;
    private String role;
    private byte[] profilePicture;
    private String bio;
    private String twitter;
    private String instagram;
    private String github;
    private String linkedin;
}
