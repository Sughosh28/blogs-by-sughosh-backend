package com.blog.blogApplication.jwt;

import com.blog.blogApplication.model.Users;
import com.blog.blogApplication.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
