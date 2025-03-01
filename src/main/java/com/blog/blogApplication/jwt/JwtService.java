package com.blog.blogApplication.jwt;

import com.blog.blogApplication.repository.UsersRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class JwtService {
    @Autowired
    private UsersRepository userRepo;

    private static final String SECRET = "D0B198607D3B3EA031DA0816E0E0865921BECFF1E50A6CAC045F816A6F9754F8E0964C0BB5EEC948C05D543623F60FC94AED8AD4D0E196989EC0E0F622EAF2BF";

    private static final Long DURATION = TimeUnit.MINUTES.toMillis(30);


//    public String generateToken(Object user) {
//        Map<String, String> claims = new HashMap<>();
//        String email;
//
//        if (user instanceof UserDetails) {
//            email = ((BlogUserDetails) user).getEmail();
//
//        } else if (user instanceof OidcUser) {
//            email = ((OidcUser) user).getEmail();
//        } else {
//            throw new IllegalArgumentException("Unsupported user type");
//        }
//
//        claims.put("email", email);
//
//        return Jwts.builder()
//                .claims(claims)
//                .subject(user instanceof UserDetails ? ((UserDetails) user).getUsername() : ((OidcUser) user).getName())
//                .issuedAt(Date.from(Instant.now()))
//                .expiration(Date.from(Instant.now().plusMillis(DURATION)))
//                .signWith(generateKey())
//                .compact();
//    }


public String generateToken(UserDetails userDetails) {
    Map<String, String> claims = new HashMap<>();
    String email = ((BlogUserDetails) userDetails).getEmail();
    Long userId=userRepo.findIdByUsername(userDetails.getUsername());


    claims.put("email", email);
    claims.put("userId", userId.toString());
    return Jwts.builder()
            .claims(claims)
            .subject(userDetails.getUsername())
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(Instant.now().plusMillis(DURATION)))
            .signWith(generateKey())
            .compact();
}


    private SecretKey generateKey() {
        byte[] decodedKey = Base64.getDecoder().decode(SECRET);
        return Keys.hmacShaKeyFor(decodedKey);
    }

    public String extractUsername(String jwt) {
        Claims claims = getClaims(jwt);
        return claims.getSubject();
    }

    public Long extractUserId(String jwt){
    Claims claims= getClaims(jwt);
    String userIdClaim = claims.get("userId", String.class);
        return Long.parseLong(userIdClaim);
    }

    public String extractEmail(String jwt) {
        Claims claims = getClaims(jwt);
        String email= claims.get("email", String.class);
        System.out.println(email);
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email not found in JWT claims");
        }

        return email;
    }


    private Claims getClaims(String jwt) {
        Claims claims=Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
        return claims;
    }

    public boolean isTokenValid(String jwt) {
        Claims claims=getClaims(jwt);
        return claims.getExpiration().after(Date.from(Instant.now()));
    }



}