package com.blog.blogApplication.Repository;

import com.blog.blogApplication.Model.Comments;
import com.blog.blogApplication.Model.Posts;
import com.blog.blogApplication.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, Long> {
    List<Comments> findByPost(Posts post);

    List<Comments> findByUser(Users user);

    List<Comments> findByParentCommentId(Long parentCommentId);
}
