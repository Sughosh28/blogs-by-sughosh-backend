package com.blog.blogApplication.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String fullName;

    @NotNull
    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    @Email
    @Valid
    @JsonIgnore

    private String email;

    @NotNull
    @JsonIgnore
    @Column(nullable = false)
    @Size(min = 8, message = "Password length should should be at least 8 characters. ")
    private String password;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Lob
    private byte[] profilePicture;
    private String profile_picture_name;
    private String imageType;

    @Column(length = 80)
    private String bio;

    private String twitter;
    private String instagram;
    private String github;
    private String linkedin;

    private String otp;
    private LocalDateTime otpExpiry;

    @OneToMany(mappedBy = "author")
    @JsonIgnore
    @ToString.Exclude
    private List<Posts> posts;



    public enum Role{
        ADMIN,
        READER,
        AUTHOR;
    }
}
