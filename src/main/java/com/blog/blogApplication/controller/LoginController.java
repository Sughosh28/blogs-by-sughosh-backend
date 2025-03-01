package com.blog.blogApplication.controller;

import com.blog.blogApplication.jwt.BlogUserDetailService;

import com.blog.blogApplication.jwt.JwtService;
import com.blog.blogApplication.records.LoginForm;
import com.blog.blogApplication.repository.UsersRepository;
import com.blog.blogApplication.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class LoginController {
    @Autowired
    private MailService mailService;
    @Autowired
    private UsersRepository repo;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private BlogUserDetailService blogUserDetailService;

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@RequestBody LoginForm loginForm) {
        return mailService.userLogin(loginForm);
    }


    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestParam String email) {
        return mailService.tokenValidationAndOtp(email);
    }
}
