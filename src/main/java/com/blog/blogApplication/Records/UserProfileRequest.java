package com.blog.blogApplication.Records;


public record UserProfileRequest(  String username,
                                   String email,
                                   String oldPassword,
                                   String newPassword) {
}
