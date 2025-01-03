package com.blog.blogApplication.DTO;

import com.blog.blogApplication.Model.Posts;
import com.blog.blogApplication.Model.Users;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private String content;
    private Users user;
    private Posts post;
    @Column(nullable = false)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime created_Time;
    @JsonFormat(pattern = "yyyy-MMM-dd")
    private LocalDate created_Date;
}
