package com.blog.blogApplication;


import io.jsonwebtoken.Jwts;
import jakarta.xml.bind.DatatypeConverter;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

public class JwtSecretKeyTest {

    @Test
    public void generateSecretKey(){
        SecretKey secretKey =Jwts.SIG.HS512.key().build();
        String decodedKey= DatatypeConverter.printHexBinary(secretKey.getEncoded());
        System.out.println(decodedKey);
    }
}
