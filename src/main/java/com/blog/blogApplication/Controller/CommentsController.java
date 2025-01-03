package com.blog.blogApplication.Controller;


import com.blog.blogApplication.DTO.CommentDTO;
import com.blog.blogApplication.JwtFiles.JwtService;
import com.blog.blogApplication.Model.Comments;
import com.blog.blogApplication.Service.CommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentsController {

    @Autowired
    private CommentsService service;
    @Autowired
    private JwtService jwtService;


    @PostMapping("/{post_id}")
    public ResponseEntity<?> createComment(@PathVariable Long post_id, @RequestBody CommentDTO dto, @RequestHeader("Authorization") String token){
        if (token == null || !token.startsWith("Bearer ")) {
            return (ResponseEntity<byte[]>) ResponseEntity.notFound();
        }
        String authToken = token.substring(7);
        Long userId= jwtService.extractUserId(authToken);
        ResponseEntity<?> comment= service.createComment(userId, post_id, dto, token);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comments>> getCommentsByPost(@PathVariable Long postId) {
        List<Comments> comments =service.getCommentsByPost(postId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Comments>> getCommentsByUser(@PathVariable Long userId) {
        List<Comments> comments = service.getCommentsByUser(userId);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        service.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Comments> editComment(@PathVariable Long commentId, @RequestBody String newContent) {
        Comments updatedComment = service.editComment(commentId, newContent);
        return ResponseEntity.ok(updatedComment);
    }

    @GetMapping("/replies/{parentCommentId}")
    public ResponseEntity<List<Comments>> getReplies(@PathVariable Long parentCommentId) {
        List<Comments> replies = service.getReplies(parentCommentId);
        return ResponseEntity.ok(replies);
    }
}
