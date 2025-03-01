package com.blog.blogApplication.repository;

import com.blog.blogApplication.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<Users> findByUsername(String username);

    Optional<Users> findByEmail(String email);

    List<Users> findByUsernameContainingIgnoreCase(String username);

    @Query("SELECT u.id FROM Users u WHERE u.username = :username")
    Long findIdByUsername(@Param("username") String username);}
