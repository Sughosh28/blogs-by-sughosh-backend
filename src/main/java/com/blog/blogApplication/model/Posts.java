package com.blog.blogApplication.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Posts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String content;

    @Lob
    private byte[] picture_content;
    private String picture_name;
    private String imageType;

    @ManyToOne
    @JoinColumn(name="author_id", nullable = false)
    private Users author;

    private String authorName;

//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
//    private LocalDateTime created_at;
@JsonFormat(pattern = "HH:mm")
private LocalTime createdTime;
    @JsonFormat(pattern = "yyyy-MMM-dd")
    private LocalDate createdDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updated_at;

    @Column(nullable = true)
    private boolean status;

    private String image_url;
    private Long views;

}
