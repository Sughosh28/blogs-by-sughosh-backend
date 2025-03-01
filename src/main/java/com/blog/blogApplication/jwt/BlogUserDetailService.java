package com.blog.blogApplication.JwtFiles;

import com.blog.blogApplication.Model.Users;
import com.blog.blogApplication.Repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BlogUserDetailService implements UserDetailsService {

    @Autowired
    UsersRepository usersRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = usersRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            return new BlogUserDetails(
                    user.getUsername(),
                    user.getPassword(),
                    user.getEmail());
    }
}
