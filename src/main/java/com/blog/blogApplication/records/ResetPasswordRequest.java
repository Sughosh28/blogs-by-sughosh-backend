package com.blog.blogApplication.records;

public record ResetPasswordRequest(
        String otp,
        String newPassword
) {
}
