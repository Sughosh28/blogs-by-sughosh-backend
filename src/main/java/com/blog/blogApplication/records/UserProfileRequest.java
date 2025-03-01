package com.blog.blogApplication.records;


public record UserProfileRequest(  String username,
                                   String email,
                                   String oldPassword,
                                   String newPassword) {
}
