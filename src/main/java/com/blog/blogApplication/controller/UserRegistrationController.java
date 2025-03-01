package com.blog.blogApplication.Controller;

import com.blog.blogApplication.DTO.UsersDTO;
import com.blog.blogApplication.Model.Users;
import com.blog.blogApplication.Service.UsersService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
public class UserRegistrationController {
    @Autowired
    private UsersService service;

    @PostMapping("/register")
    public ResponseEntity<Users> registerUser(@RequestBody @Valid UsersDTO userDTO, Model model){
        return service.registerUser(userDTO);
    }

}
