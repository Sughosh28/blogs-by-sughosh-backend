package com.blog.blogApplication.JwtFiles;

import com.blog.blogApplication.Model.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogUserDetails implements UserDetails {

    private String username;
    private String password;
    private String email;
    private Collection<? extends GrantedAuthority> authorities;

    public BlogUserDetails(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.authorities = authorities;
    }

    public static BlogUserDetails fromUser(Users user) {
        return new BlogUserDetails(
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
    public BlogUserDetails(String email) {
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
