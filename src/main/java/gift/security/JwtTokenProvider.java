package gift.security;

import gift.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.util.Date;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
    private final SecretKey secretKey;

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    public JwtTokenProvider(@Value("${jwt.secret.key}") String secretKeyString) {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    public String generateToken(Member member) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + Duration.ofHours(1).toMillis());
        return Jwts.builder()
                .subject(member.getId().toString())
                .claim("email", member.getEmail())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(this.secretKey)
                .compact();
    }

    public boolean validateToken(String accessToken) {
        try {
            getPayload(accessToken);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    public String getSubject(String accessToken) {
        return getPayload(accessToken).getSubject();
    }

    private Claims getPayload(String accessToken) {
        return Jwts.parser()
                .verifyWith(this.secretKey)
                .build()
                .parseSignedClaims(accessToken)
                .getPayload();
    }
}
