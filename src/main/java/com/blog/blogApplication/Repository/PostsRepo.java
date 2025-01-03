package com.blog.blogApplication.Repository;

import com.blog.blogApplication.Model.Posts;
import com.blog.blogApplication.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostsRepo extends JpaRepository<Posts, Long> {
    List<Posts> findByAuthorNot(Users user);

    List<Posts> findByAuthor(Users user);
}
