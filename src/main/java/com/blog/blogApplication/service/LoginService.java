package com.blog.blogApplication.Service;

import com.blog.blogApplication.JwtFiles.JwtService;
import com.blog.blogApplication.Repository.UsersRepository;
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
