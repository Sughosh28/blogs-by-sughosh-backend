package com.blog.blogApplication.Service;

import com.blog.blogApplication.DTO.PostsDTO;
import com.blog.blogApplication.DTO.UserProfileDTO;
import com.blog.blogApplication.JwtFiles.JwtService;
import com.blog.blogApplication.Model.Posts;
import com.blog.blogApplication.Model.Users;
import com.blog.blogApplication.Repository.PostsRepo;
import com.blog.blogApplication.Repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostsService {

    @Autowired
    private UsersRepository userRepo;

    @Autowired
    private PostsRepo repo;
    @Autowired
    private JwtService jwtService;


    public ResponseEntity<?> createPost(PostsDTO dto,MultipartFile file, String token) throws IOException {
        if (token == null || !token.startsWith("Bearer ")) {
            return new ResponseEntity<>("Authorization token is missing or invalid.", HttpStatus.UNAUTHORIZED);
        }

        String jwtToken = token.substring(7);
        String username;

        try {
            username = jwtService.extractUsername(jwtToken);
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid token.", HttpStatus.UNAUTHORIZED);
        }

        Optional<Users> author = userRepo.findByUsername(username);

        if (author.isEmpty()) {
            return new ResponseEntity<>("You need to have an account to post.", HttpStatus.NOT_FOUND);
        }
        Users user = author.get();

        Map<String, Object> fileDetails = saveFile( file);

        Posts newPost = new Posts();
        newPost.setTitle(dto.getTitle());
        newPost.setContent(dto.getContent());
        newPost.setAuthor(user);
        newPost.setAuthorName(username);
        newPost.setStatus(dto.isStatus());
        newPost.setImage_url(dto.getImageUrl());
        newPost.setCreatedTime(LocalTime.now());
        newPost.setCreatedDate(LocalDate.now());
        newPost.setPicture_name((String) fileDetails.get("picture_name"));
        newPost.setImageType((String) fileDetails.get("imageType"));
        newPost.setPicture_content((byte[]) fileDetails.get("picture_content"));
        repo.save(newPost);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Post created successfully.");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    public ResponseEntity<List<PostsDTO>> getAllPosts(Long user_id) throws IOException {

        Optional<Users> userOptional = userRepo.findById(user_id);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Users user = userOptional.get();

        List<Posts> posts = repo.findByAuthorNot(user);


        List<PostsDTO> postDTOs = posts.stream()
                .map(post -> convertToDTO(post))
                .collect(Collectors.toList());

        return new ResponseEntity<>(postDTOs, HttpStatus.OK);
    }

    private PostsDTO convertToDTO(Posts post) {
        PostsDTO dto = new PostsDTO();
        dto.setId(post.getId());
        dto.setAuthorName(post.getAuthorName());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setImageUrl(post.getImage_url());
        dto.setPicture_content(post.getPicture_content());
        dto.setPicture_name(post.getPicture_name());
        dto.setImageType(post.getImageType());
        dto.setStatus(post.isStatus());
        dto.setCreatedTime(post.getCreatedTime());
        dto.setCreatedDate(post.getCreatedDate());
        dto.setUpdatedAt(post.getUpdated_at());
        return dto;
    }

    public ResponseEntity<?> editPosts(Long id, PostsDTO dto, String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return new ResponseEntity<>("Authorization token is missing or invalid.", HttpStatus.UNAUTHORIZED);
        }

        String jwtToken = token.substring(7);
        String username;

        try {
            username = jwtService.extractUsername(jwtToken);
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid token.", HttpStatus.UNAUTHORIZED);
        }
        Optional<Users> author = userRepo.findByUsername(username);

        if (author.isEmpty()) {
            return new ResponseEntity<>("Unauthenticated.", HttpStatus.NOT_FOUND);
        }
        Users user = author.get();

        Optional<Posts> post = repo.findById(id);
        if (post.isEmpty()) {
            return new ResponseEntity<>("There are no posts to edit.", HttpStatus.NOT_FOUND);
        } else {
            Posts editedPost = post.get();
            if (!editedPost.getAuthor().getUsername().equals(username)) {
                return new ResponseEntity<>("You are not authorized to edit this post", HttpStatus.FORBIDDEN);
            }
            editedPost.setTitle(dto.getTitle());
            editedPost.setContent(dto.getContent());
            editedPost.setAuthorName(username);
            editedPost.setAuthor(user);
            editedPost.setStatus(dto.isStatus());
            editedPost.setImage_url(dto.getImageUrl());
            editedPost.setUpdated_at(LocalDateTime.now());

            repo.save(editedPost);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Post updated successfully.");
            response.put("post", editedPost);
            return new ResponseEntity<>(response, HttpStatus.OK);

        }
    }

    public ResponseEntity<?> deletePosts(Long id, String token) {

        if (token == null || !token.startsWith("Bearer ")) {
            return new ResponseEntity<>("Authorization token is missing or invalid.", HttpStatus.UNAUTHORIZED);
        }

        String jwtToken = token.substring(7);
        String username;

        try {
            username = jwtService.extractUsername(jwtToken);
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid token.", HttpStatus.UNAUTHORIZED);
        }

        Optional<Posts> post = repo.findById(id);
        if (post.isEmpty()) {
            return new ResponseEntity<>("Post not found", HttpStatus.NOT_FOUND);
        }
        Posts postEntity = post.get();

        if (!postEntity.getAuthor().getUsername().equals(username)) {
            return new ResponseEntity<>("You are not authorized to delete this post", HttpStatus.FORBIDDEN);
        }

        repo.deleteById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Post deleted successfully.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    public Map<String, Object> saveFile( MultipartFile fileToSave) throws IOException {
        if (fileToSave == null) {
            throw new RuntimeException("File is null");
        }

        Map<String, Object> fileDetails = new HashMap<>();
        fileDetails.put("picture_name", fileToSave.getOriginalFilename());
        fileDetails.put("imageType", fileToSave.getContentType());
        fileDetails.put("picture_content", fileToSave.getBytes());

        return fileDetails;
    }

    public ResponseEntity<?> getPost(Long id, String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return new ResponseEntity<>("Authorization token is missing or invalid.", HttpStatus.UNAUTHORIZED);
        }

        String jwtToken = token.substring(7);
        String username;

        try {
            username = jwtService.extractUsername(jwtToken);
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid token.", HttpStatus.UNAUTHORIZED);
        }

        Optional<Posts> post = repo.findById(id);
        if (post.isEmpty()) {
            return new ResponseEntity<>("Post not found", HttpStatus.NOT_FOUND);
        }

        Posts postEntity = post.get();
        PostsDTO postDTO = convertToDTO(postEntity);

        Map<String, Object> response = new HashMap<>();
        response.put("post", postDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> getProfile(Long id, String token) {
        String username;

        try {
            username = jwtService.extractUsername(token);
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid token.", HttpStatus.UNAUTHORIZED);
        }

        Optional<Users> userOptional = userRepo.findById(id);
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        Users user = userOptional.get();
        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setFullName(user.getFullName());
        profileDTO.setUsername(user.getUsername());
        profileDTO.setEmail(user.getEmail());
        profileDTO.setBio(user.getBio());
        profileDTO.setProfilePicture(user.getProfilePicture());

        List<Posts> userPosts = repo.findByAuthor(user);
        List<PostsDTO> postDTOs = userPosts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("profile", profileDTO);
        response.put("posts", postDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

