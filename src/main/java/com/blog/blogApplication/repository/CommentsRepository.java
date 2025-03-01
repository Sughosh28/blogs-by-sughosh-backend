package com.blog.blogApplication.repository;

import com.blog.blogApplication.model.Comments;
import com.blog.blogApplication.model.Posts;
import com.blog.blogApplication.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, Long> {
    List<Comments> findByPost(Posts post);

    List<Comments> findByUser(Users user);

    List<Comments> findByParentCommentId(Long parentCommentId);
}
