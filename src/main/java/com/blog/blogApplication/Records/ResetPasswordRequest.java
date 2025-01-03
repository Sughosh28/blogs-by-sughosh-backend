package com.blog.blogApplication.Records;

public record ResetPasswordRequest(
        String otp,
        String newPassword
) {
}
