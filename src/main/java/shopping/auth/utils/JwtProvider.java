package shopping.auth.utils;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private Long expirationMilliSeconds;

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
