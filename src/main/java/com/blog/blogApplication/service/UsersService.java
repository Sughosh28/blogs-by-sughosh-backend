package com.blog.blogApplication.Service;

import com.blog.blogApplication.DTO.AuthResponse;
import com.blog.blogApplication.DTO.UserProfileDTO;
import com.blog.blogApplication.DTO.UsernameUpdateDTO;
import com.blog.blogApplication.DTO.UsersDTO;
import com.blog.blogApplication.JwtFiles.BlogUserDetailService;
import com.blog.blogApplication.JwtFiles.JwtService;
import com.blog.blogApplication.Model.Users;
import com.blog.blogApplication.Records.LoginForm;
import com.blog.blogApplication.Repository.UsersRepository;
import com.blog.blogApplication.Records.UserProfileRequest;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class UsersService {

    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UsersRepository repo;
    @Autowired
    private BlogUserDetailService blogUserDetailService;

    @Autowired
    private MailService mailService;

    public ResponseEntity<Users> registerUser(@Valid UsersDTO userDTO) {
        if (repo.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("User already exists. Try logging in.");
        }

        if (repo.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("User already exists. Try logging in.");
        }
        String encodedPassword = encoder.encode(userDTO.getPassword());

        Users newUser = new Users();
        newUser.setFullName(userDTO.getFullName());
        newUser.setUsername(userDTO.getUsername());
        newUser.setPassword(encodedPassword);
        newUser.setEmail(userDTO.getEmail());
        newUser.setRole(Users.Role.valueOf(userDTO.getRole()));
        newUser.setBio(userDTO.getBio());
        newUser.setGithub(userDTO.getGithub());
        newUser.setInstagram(userDTO.getInstagram());
        newUser.setLinkedin(userDTO.getLinkedin());
        newUser.setTwitter(userDTO.getTwitter());
        repo.save(newUser);
        mailService.sendWelcomeEmailWithHtml(userDTO.getEmail(), userDTO.getFullName());
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    public ResponseEntity<?> userLogin(LoginForm loginForm) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginForm.username(), loginForm.password()));

            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(blogUserDetailService.loadUserByUsername(loginForm.username()));
                return new ResponseEntity<>(new AuthResponse(token), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
            }
        } catch (BadCredentialsException e) {

            return new ResponseEntity<>("Invalid credentials!", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Invalid credentials!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getUserProfile(String token) {
        try {
            String authToken = token.replace("Bearer ", "");

            String userName = jwtService.extractUsername(authToken);
            Optional<Users> user = repo.findByUsername(userName);

            if (user.isPresent()) {
                Users users = user.get();

                UserProfileDTO userDto = new UserProfileDTO(
                        users.getFullName(),
                        users.getUsername(),
                        users.getEmail(),
                        users.getRole(),
                        users.getProfilePicture(),
                        users.getBio(),
                        users.getTwitter(),
                        users.getInstagram(),
                        users.getGithub(),
                        users.getLinkedin());
                return new ResponseEntity<>(userDto, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User not found ", HttpStatus.NOT_FOUND);
            }
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("JWT token has expired", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }
    }

    public ResponseEntity<?> updateUserProfile(UsersDTO userDTO, String token) {

        try {
            String authToken = token.replace("Bearer ", "");
            String tokenUsername = jwtService.extractUsername(authToken);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedUsername = authentication.getName();

            if (!authenticatedUsername.equals(tokenUsername)) {
                return new ResponseEntity<>("Invalid token or authentication mismatch", HttpStatus.FORBIDDEN);
            }

            if (!authenticatedUsername.equals(userDTO.getUsername())) {
                return new ResponseEntity<>("You are not authorized to update this profile", HttpStatus.FORBIDDEN);
            }

            Optional<Users> userOptional = repo.findByUsername(authenticatedUsername);
            if (userOptional.isEmpty()) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

            Users userEntity = userOptional.get();
            userEntity.setFullName(userDTO.getFullName());
            userEntity.setEmail(userDTO.getEmail());

            try {
                userEntity.setRole(Users.Role.valueOf(userDTO.getRole().toUpperCase()));
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>("Invalid role", HttpStatus.BAD_REQUEST);
            }

            userEntity.setBio(userDTO.getBio());
            userEntity.setGithub(userDTO.getGithub());
            userEntity.setInstagram(userDTO.getInstagram());
            userEntity.setLinkedin(userDTO.getLinkedin());
            userEntity.setTwitter(userDTO.getTwitter());
            userEntity.setFullName(userDTO.getFullName());

            repo.save(userEntity);
            System.out.println("Incoming UserDTO Data: " + userDTO);



            return new ResponseEntity<>("Profile updated successfully", HttpStatus.OK);

        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("JWT token has expired", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while updating the profile", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<?> changePassword(UserProfileRequest request, String token) {

        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        String mail;
        try {
            mail = jwtService.extractEmail(jwt);
            System.out.println(mail);
        } catch (IllegalArgumentException | MalformedJwtException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid token");
        }

        if (mail == null || mail.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found in token");
        }
        Optional<Users> userMail = repo.findByEmail(mail);
        if (userMail.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Users userEntity = userMail.get();
        if (encoder.matches(request.oldPassword(), userEntity.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("New password can't be same as old password.");
        }

        String encodedPassword = encoder.encode(request.newPassword());
        userEntity.setPassword(encodedPassword);
        repo.save(userEntity);

        return ResponseEntity.ok("Password updated successfully!");
    }


    public ResponseEntity<?> updateUsername(String username, UsernameUpdateDTO usernameUpdateDTO, String token) {
        try {
            String authToken = token.replace("Bearer ", "");
            // Extract the authenticated username from the token
            String authenticatedUsername = jwtService.extractUsername(authToken);

            if (authenticatedUsername == null || authenticatedUsername.isEmpty()) {
                return new ResponseEntity<>("Invalid or expired token", HttpStatus.UNAUTHORIZED);
            }

            // Find user by username
            Optional<Users> userOptional = repo.findByUsername(username);
            if (userOptional.isEmpty()) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

            Users userEntity = userOptional.get();

            // Ensure the authenticated user is the same as the one being updated
            if (!userEntity.getUsername().equals(authenticatedUsername)) {
                return new ResponseEntity<>("You are not authorized to update this user's information", HttpStatus.FORBIDDEN);
            }

            // Validate the new username
            String newUsername = usernameUpdateDTO.getNewUsername();
            if (newUsername == null || newUsername.trim().isEmpty()) {
                return new ResponseEntity<>("Invalid username", HttpStatus.BAD_REQUEST);
            }

            // Check if the new username is already taken by another user
            if (repo.findByUsername(newUsername).isPresent()) {
                return new ResponseEntity<>("Username is already taken", HttpStatus.CONFLICT);
            }

            // Update and save the user
            userEntity.setUsername(newUsername);
            repo.save(userEntity);

            return new ResponseEntity<>("Username updated successfully", HttpStatus.OK);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("JWT token has expired", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while updating the username", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void saveFile(Long id, MultipartFile fileToSave) throws IOException {

        if (fileToSave == null) {
            throw new RuntimeException("File is null");
        }
        Optional<Users> userEntity = repo.findById(id);
        if (userEntity.isEmpty()) {
            throw new RuntimeException("Unauthorised User");
        }

        Users user = userEntity.get();
        user.setProfile_picture_name(fileToSave.getOriginalFilename());
        user.setImageType(fileToSave.getContentType());
        user.setProfilePicture(fileToSave.getBytes());
        new ResponseEntity<>(repo.save(user), HttpStatus.OK);

    }

    public List<Users> searchUsers(String username) {
        return repo.findByUsernameContainingIgnoreCase(username);
    }

}
