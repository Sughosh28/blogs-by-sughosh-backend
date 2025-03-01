package com.blog.blogApplication.service;

import com.blog.blogApplication.jwt.JwtService;
import com.blog.blogApplication.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {


    @Autowired
    private MailService mailService;
    @Autowired
    private UsersRepository repo;
    @Autowired
    private JwtService jwtService;


    }
