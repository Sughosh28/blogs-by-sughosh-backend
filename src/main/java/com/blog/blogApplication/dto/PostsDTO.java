package com.blog.blogApplication.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostsDTO {
    private Long id;
    private String title;
    private String content;
    private String authorName;
    private String imageUrl;
    private byte[] picture_content;
    private String picture_name;
    private String imageType;
    private boolean status;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime createdTime;
    @JsonFormat(pattern = "yyyy-MMM-dd")
    private LocalDate createdDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
