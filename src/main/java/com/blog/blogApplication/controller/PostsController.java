package com.blog.blogApplication.controller;

import com.blog.blogApplication.dto.PostsDTO;
import com.blog.blogApplication.jwt.JwtService;
import com.blog.blogApplication.model.Posts;
import com.blog.blogApplication.repository.PostsRepo;
import com.blog.blogApplication.service.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/posts")
public class PostsController {
    private static final Logger log = Logger.getLogger(PostsController.class.getName());

    @Autowired
    private PostsService postsService;

    @Autowired
    private PostsRepo repo;

    @Autowired
    private JwtService jwtService;

    @PostMapping(value = "/createPost", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createPost(
            @RequestPart PostsDTO dto,
            @RequestPart(required = false) MultipartFile file,
            @RequestHeader("Authorization") String token) throws IOException {
        log.info("Received DTO: {}");
        log.info("File received: {}");
        log.info("Request Content-Type: {}");
        return postsService.createPost(dto, file, token);
    }

    @GetMapping("/feedPosts")
    public ResponseEntity<?> getAllPosts(
            @RequestHeader("Authorization") String token) throws IOException {
        String authToken = token.substring(7);
        Long userId = jwtService.extractUserId(authToken);

        ResponseEntity<List<PostsDTO>> posts = postsService.getAllPosts(userId);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @PutMapping("/edit-post/{id}")
    public ResponseEntity<?> editPost(@PathVariable Long id,
                                      @RequestBody PostsDTO dto, @RequestHeader("Authorization") String token) {
        return postsService.editPosts(id, dto, token);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        return postsService.deletePosts(id, token);
    }


    @PostMapping("/upload-picture-content")
    public ResponseEntity<?> uploadFile(@RequestPart
                                        MultipartFile file, @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            System.out.println(jwtToken);

            Long userId = jwtService.extractUserId(jwtToken);
            postsService.saveFile(file);
            return new ResponseEntity<>("Content uploaded successfully.", HttpStatus.OK);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return new ResponseEntity<>("Could not set the content picture.", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/picture-content/{id}")
    public ResponseEntity<byte[]> getUserPicture(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return (ResponseEntity<byte[]>) ResponseEntity.notFound();
        }

        String jwtToken = token.substring(7);
        String username;

        try {
            username = jwtService.extractUsername(jwtToken);
        } catch (Exception e) {
            return (ResponseEntity<byte[]>) ResponseEntity.notFound();
        }
        Optional<Posts> postEntity = repo.findById(id);
        Posts post = postEntity.get();
        if (!post.getAuthor().getUsername().equals(username)) {
            return (ResponseEntity<byte[]>) ResponseEntity.notFound();
        }

        byte[] imageFile = post.getPicture_content();
        return ResponseEntity.ok().contentType(MediaType.valueOf(post.getImageType()))
                .body(imageFile);
    }


    @GetMapping("/get-post/{id}")
    public ResponseEntity<?> getPost(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        return postsService.getPost(id, token);
    }

    @GetMapping("/getUserProfile/{id}")
    public ResponseEntity<?> getProfile(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return (ResponseEntity<byte[]>) ResponseEntity.notFound();
        }
        String authToken=token.substring(7);
        return postsService.getProfile(id, authToken);
    }


}
