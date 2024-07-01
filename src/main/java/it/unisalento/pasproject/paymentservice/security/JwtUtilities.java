package it.unisalento.pasproject.paymentservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import static it.unisalento.pasproject.paymentservice.security.SecurityConstants.JWT_SECRET;

/**
 * The JwtUtilities class provides utility methods for handling JSON Web Tokens (JWTs).
 * It includes methods for extracting claims from a token, validating a token, and checking if a token is expired.
 */
@Service
public class JwtUtilities {
    /**
     * The key used for signing JWTs.
     */
    private final Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());

    /**
     * The logger used for logging messages.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtilities.class);

    /**
     * Extracts the username from the given JWT.
     * @param token the JWT
     * @return the username
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    /**
     * Extracts the expiration date from the given JWT.
     * @param token the JWT
     * @return the expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a claim from the given JWT using the provided claims' resolver.
     * @param token the JWT
     * @param claimsResolver the claims resolver
     * @return the claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the given JWT.
     * @param token the JWT
     * @return the claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Checks if the given JWT is expired.
     * @param token the JWT
     * @return true if the JWT is expired, false otherwise
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validates the given JWT against the provided UserDetails.
     * @param token the JWT
     * @param userDetails the UserDetails
     * @return true if the JWT is valid, false otherwise
     */
    public Boolean validateToken(String token, UserDetails userDetails, String role) {
        final String username = extractUsername(token);
        final String tokenRole = extractRole(token);
        return (username.equals(userDetails.getUsername()) && tokenRole.equalsIgnoreCase(role)  && !isTokenExpired(token));
    }
}
