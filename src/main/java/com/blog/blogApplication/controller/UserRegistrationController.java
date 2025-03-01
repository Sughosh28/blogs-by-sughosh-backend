package com.blog.blogApplication.controller;

import com.blog.blogApplication.dto.UsersDTO;
import com.blog.blogApplication.model.Users;
import com.blog.blogApplication.service.UsersService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRegistrationController {
    @Autowired
    private UsersService service;

    @PostMapping("/register")
    public ResponseEntity<Users> registerUser(@RequestBody @Valid UsersDTO userDTO, Model model){
        return service.registerUser(userDTO);
    }

}
