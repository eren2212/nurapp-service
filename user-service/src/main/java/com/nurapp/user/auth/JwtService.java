package com.nurapp.user.auth;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private final SecretKey key;
	private final long accessTtlMinutes;

	public JwtService(@Value("${app.jwt.secret}") String secret,
			@Value("${app.jwt.access-ttl-minutes}") long accessTtlMinutes) {
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.accessTtlMinutes = accessTtlMinutes;
	}

	public String issueAccessToken(UUID userId) {
		Instant now = Instant.now();
		return Jwts.builder().subject(userId.toString()).issuedAt(Date.from(now))
				.expiration(Date.from(now.plus(accessTtlMinutes, ChronoUnit.MINUTES))).signWith(key).compact();
	}

	public UUID parseUserId(String token) {
		Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
		return UUID.fromString(claims.getSubject());
	}
}