package com.gugucon.shopping.member.utils;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtProvider {

    private final String secretKey;
    private final Long expirationMilliSeconds;

    public JwtProvider(final String secretKey, final Long expirationMilliSeconds) {
        this.secretKey = secretKey;
        this.expirationMilliSeconds = expirationMilliSeconds;
    }

    public String generateToken(final String subject) {
        Date now = new Date(System.currentTimeMillis());
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(new Date(now.getTime() + expirationMilliSeconds))
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public boolean validate(final String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration()
                    .after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String parseToken(final String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
