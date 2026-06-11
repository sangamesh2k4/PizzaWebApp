package com.pizzaapp.PizzaWebApp.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static io.jsonwebtoken.Header.ALGORITHM;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-expiration}")
    private  Long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;


  /*  private static final String SECRET_KEY="my-super-secret-jwt-key-for-pizza-app-project-2026";

    private static final long ACCESS_TOKEN_EXPIRATION=1000*60*15;
    private static final long REFRESH_TOKEN_EXPIRATION=1000L*60*60*24*30;
*/


    //generate access token
    public String generateAccessToken(UserDetails userDetails){

        Map<String,Object> claims=new HashMap<>();
        claims.put("roles",userDetails.getAuthorities());
        return buildToken(claims,userDetails,accessExpiration);
    }

    //generate refresh token
    public String generateRefreshToken(UserDetails userDetails){
        return buildToken(new HashMap<>(),userDetails,refreshExpiration);
    }

    //common token builder
    private String buildToken(Map<String,Object> extraClaims,UserDetails userDetails, Long expiration){
        return Jwts.builder().claims(extraClaims).subject(userDetails.getUsername()).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis()+expiration)).signWith(getSignInkey()).compact();
    }

    //extract username from JWT
    public String extractUsername(String token){
        return extractClaim(
                token,
                Claims::getSubject
        );
    }

    //extract expiration date
    public Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }

    //generic claim extractor
    public <T> T extractClaim(String token, Function<Claims, T>resolver){
        Claims claims=extratAllClaims(token);
        return resolver.apply(claims);
    }

    //parse all jwt claims
    private Claims extratAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getSignInkey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    //check if token is expired
    public boolean isTokenExpired(String token){
        return extractExpiration(token)
                .before(new Date());
    }

    //check if token validate
    public boolean isTokenValid(String token, UserDetails userDetails){
        String username=extractUsername(token);
        return username.equals(userDetails.getUsername())&& !isTokenExpired(token);
    }

    //convert secret string into signing key
    private SecretKey getSignInkey(){
        //byte[] KeyBytes= Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}
