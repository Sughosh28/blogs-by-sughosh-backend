package com.blog.blogApplication.service;

import com.blog.blogApplication.dto.AuthResponse;
import com.blog.blogApplication.jwt.BlogUserDetailService;
import com.blog.blogApplication.jwt.JwtService;
import com.blog.blogApplication.model.Users;
import com.blog.blogApplication.records.LoginForm;
import com.blog.blogApplication.repository.UsersRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;


@Service
public class MailService {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JavaMailSender sender;

    @Autowired
    private UsersRepository repo;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private BlogUserDetailService blogUserDetailService;



    public void sendWelcomeEmailWithHtml(String email, String fullName) {

        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("sughoshathreya1@gmail.com");
            helper.setTo(email);
            helper.setSubject("Sughosh from Blog");
            Context context = new Context();
            context.setVariable("username", fullName);

            String htmlContent = templateEngine.process("welcome", context);

            helper.setText(htmlContent, true);
            sender.send(message);

        }
        catch (MailException | MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public String sendEmailWithAttachment(@PathVariable String email) {
        Optional<Users> user = repo.findByEmail(email);
        if(user.isEmpty()){
            return "Email not registered!";
        }
        Users userEntity= user.get();
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
        userEntity.setOtp(otp);
        userEntity.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        repo.save(userEntity);
        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("sughoshathreya1@gmail.com");
            helper.setTo(email);
            helper.setSubject("OTP for changing the password.");
            helper.setText("Your OTP for password reset is: " + otp);
            helper.setReplyTo("sughoshathreya1@gmail.com");
            sender.send(message);
            return "Success";
        } catch (MailException | MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<?> sendEmailWithHtml(@PathVariable String email, String otp) {

        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("sughoshathreya1@gmail.com");
            helper.setTo(email);
            helper.setSubject("OTP for password reset.");
            helper.setText("Your OTP for password reset is: " +otp );
            Context context = new Context();
            context.setVariable("otp", otp);
            String htmlContent = templateEngine.process("email-content", context);
            helper.setText(htmlContent, true);
            sender.send(message);
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } catch (MailException | MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    public ResponseEntity<?> validateOtpAndResetPassword(String token, String otp, String newPassword) {
        String email;
        try {
            String authToken = token.replace("Bearer ", "");
            email = jwtService.extractEmail(authToken);
            System.out.println(email);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token!");
        }

        Optional<Users> user = repo.findByEmail(email);
        if (user.isEmpty()) {
            return new ResponseEntity<>("Email not registered!", HttpStatus.NOT_FOUND);
        }
        Users userEntity = user.get();

        if (userEntity.getOtp() == null || !userEntity.getOtp().equals(otp)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP!");
        }

        if (userEntity.getOtpExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP has expired!");
        }

        String encodedPassword = encoder.encode(newPassword);
        userEntity.setPassword(encodedPassword);
        userEntity.setOtp(null);
        userEntity.setOtpExpiry(null);
        repo.save(userEntity);

        return ResponseEntity.ok("Password reset successfully!");
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


    public ResponseEntity<?> tokenValidationAndOtp(String email){
        Optional<Users> user = repo.findByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("E-mail not registered with us.");
        }
        Users userEntity = user.get();
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
        String token = jwtService.generateToken(blogUserDetailService.loadUserByUsername(userEntity.getUsername()));

        String authToken = token.replace("Bearer ", "");
        userEntity.setOtp(otp);
        userEntity.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        repo.save(userEntity);
        if (userEntity.getOtpExpiry() != null && userEntity.getOtpExpiry().isBefore(LocalDateTime.now())) {
            userEntity.setOtp(null);
            userEntity.setOtpExpiry(null);
            repo.save(userEntity);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP expired");
        }

        String emailStatus = String.valueOf(sendEmailWithHtml(email, otp));
        return ResponseEntity.ok(new HashMap<String, Object>() {{
            put("status", emailStatus);
            put("otp", otp);
            put("token", authToken);
        }});
    }
}


