package com.blog.blogApplication.Service;

import com.blog.blogApplication.DTO.CommentDTO;
import com.blog.blogApplication.JwtFiles.JwtService;
import com.blog.blogApplication.Model.Comments;
import com.blog.blogApplication.Model.Posts;
import com.blog.blogApplication.Model.Users;
import com.blog.blogApplication.Repository.CommentsRepository;
import com.blog.blogApplication.Repository.PostsRepo;
import com.blog.blogApplication.Repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class CommentsService {
    @Autowired
    private UsersRepository userRepo;
    @Autowired
    private PostsRepo postsRepo;
    @Autowired
    private CommentsRepository commentsRepository;
    @Autowired
    private JwtService jwtService;

    public ResponseEntity<?> createComment(Long userId, Long postId, CommentDTO dto, String token) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));

        Posts post = postsRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id " + postId));

        Comments comment = new Comments();
        comment.setContent(dto.getContent());
        comment.setUser(user);
        comment.setPost(post);
        comment.setCreated_Time(LocalTime.now());
        comment.setCreated_Date(LocalDate.now());

        Comments savedComment = commentsRepository.save(comment);
        return new ResponseEntity<>(savedComment, HttpStatus.OK);
    }


    public List<Comments> getCommentsByPost(Long postId) {
        Posts post = postsRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id " + postId));

        return commentsRepository.findByPost(post);
    }

    public List<Comments> getCommentsByUser(Long userId) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));

        return commentsRepository.findByUser(user);
    }

    public void deleteComment(Long commentId) {
        Comments comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id " + commentId));

        commentsRepository.delete(comment);
    }

    public Comments editComment(Long commentId, String newContent) {
        Comments comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id " + commentId));
        comment.setContent(newContent);
        return commentsRepository.save(comment);
    }

    public List<Comments> getReplies(Long parentCommentId) {
        return commentsRepository.findByParentCommentId(parentCommentId);
    }
}
