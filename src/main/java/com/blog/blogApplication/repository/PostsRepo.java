package com.blog.blogApplication.repository;

import com.blog.blogApplication.model.Posts;
import com.blog.blogApplication.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostsRepo extends JpaRepository<Posts, Long> {
    List<Posts> findByAuthorNot(Users user);

    List<Posts> findByAuthor(Users user);
}
