package com.blog.blogApplication.DTO;

import com.blog.blogApplication.Model.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
public class UserProfileDTO {
        private String fullName;
        private String username;
        private String email;
        private Users.Role role;
        private byte[] profilePicture;
//        private String profile_picture_name;
//        private String imageType;
        private String bio;
        private String twitter;
        private String instagram;
        private String github;
        private String linkedin;


    }

