package com.blog.blogApplication.Controller;

import com.blog.blogApplication.DTO.UsernameUpdateDTO;
import com.blog.blogApplication.DTO.UsersDTO;
import com.blog.blogApplication.JwtFiles.JwtService;
import com.blog.blogApplication.Model.Users;
import com.blog.blogApplication.Records.ResetPasswordRequest;
import com.blog.blogApplication.Repository.UsersRepository;
import com.blog.blogApplication.Service.MailService;
import com.blog.blogApplication.Service.UsersService;
import com.blog.blogApplication.Records.UserProfileRequest;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;


@CrossOrigin( origins = "*")
@RestController
@RequestMapping("/api/users")
@Tag(name = "My API", description = "API for Login Signup")
public class UsersController {


    @Autowired
    private MailService mailService;

    @Autowired
    private UsersRepository repository;
    @Autowired
    private UsersService service;

    @Autowired
    private JwtService jwtService;

    private static final Logger log = Logger.getLogger(UsersController.class.getName());


    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String token) {
        return service.getUserProfile(token);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@RequestBody UsersDTO userDTO, @RequestHeader("Authorization") String token) {
        return service.updateUserProfile(userDTO, token);
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody UserProfileRequest request, @RequestHeader("Authorization") String token) {
        if (!repository.existsByEmail(request.email())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body
                    ("Invalid email address!");
        }
        return service.changePassword(request, token);
    }

    @PatchMapping("/update-username/{username}")
    public ResponseEntity<?> updateUsername(@PathVariable String username, @RequestBody UsernameUpdateDTO dto, @RequestHeader("Authorization") String token) {
        return service.updateUsername(username, dto, token);
    }


    @PostMapping("/upload-profile-picture")
    public ResponseEntity<?> uploadFile( @RequestPart
    MultipartFile file
            , @RequestHeader("Authorization") String token) {
        try {
            // Extract and validate token
            String authToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            String authenticatedUsername = jwtService.extractUsername(authToken);

            Optional<Users> userOptional = repository.findByUsername(authenticatedUsername);
            if (userOptional.isEmpty() || !userOptional.get().getUsername().equals(authenticatedUsername)) {
                return new ResponseEntity<>("Unauthorized or invalid user.", HttpStatus.FORBIDDEN);
            }
            Users user= userOptional.get();
            Long id=user.getId();

            // Save file
            service.saveFile(id,file);
            return new ResponseEntity<>("Profile picture uploaded successfully.", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ResponseEntity<>("Error saving file.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("JWT token has expired.", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/profile-picture")
    public ResponseEntity<?> getUserProfilePicture( @RequestHeader("Authorization") String token) {
        // Validate and extract username from the JWT token
        String authToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        String username = jwtService.extractUsername(authToken);

        // Check if the token is valid and username is not null
        if (username == null) {
            return new ResponseEntity<>("Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }

        // Fetch the user by ID
        Optional<Users> userEntity = repository.findByUsername(username);
        if (userEntity.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        Users user = userEntity.get();

        // Optional: Ensure that the authenticated user can access the profile picture
        if (!username.equals(user.getUsername())) {
            return new ResponseEntity<>("You are not authorized to access this profile picture", HttpStatus.FORBIDDEN);
        }

        // Fetch the profile picture
        byte[] imageFile = user.getProfilePicture();
        if (imageFile == null || imageFile.length == 0) {
            return new ResponseEntity<>("Profile picture not found", HttpStatus.NOT_FOUND);
        }

        // Validate MIME type (optional)
        String imageType = user.getImageType();
        if (imageType == null || (!imageType.equals("image/jpeg") && !imageType.equals("image/png"))) {
            return new ResponseEntity<>("Invalid image type", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(imageType))
                .body(imageFile);
    }




    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request, @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        return mailService.validateOtpAndResetPassword(token, request.otp(), request.newPassword());
    }


    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam String username, @RequestHeader("Authorization") String token) {
        // Extract and validate the JWT token
        String authToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        String userNameFromToken = jwtService.extractUsername(authToken);

        if (userNameFromToken == null) {
            return new ResponseEntity<>("Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }

        // Call the service to search for users based on the provided username
        List<Users> users = service.searchUsers(username);

        if (users.isEmpty()) {
            return new ResponseEntity<>("No users found", HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(users);
    }



}
