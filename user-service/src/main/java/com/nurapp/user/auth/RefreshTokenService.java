package com.nurapp.user.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nurapp.user.domain.RefreshToken;
import com.nurapp.user.domain.RefreshTokenRepository;

@Service
public class RefreshTokenService {

	private final RefreshTokenRepository repo;
	private final long refreshTtlDays;
	private final SecureRandom random = new SecureRandom();

	public RefreshTokenService(RefreshTokenRepository repo, @Value("${app.jwt.refresh-ttl-days}") long refreshTtlDays) {
		this.repo = repo;
		this.refreshTtlDays = refreshTtlDays;
	}

	/** Yeni refresh token üretir, hash'ini saklar, ham değeri döndürür. */
	@Transactional
	public String issue(UUID userId) {
		String raw = generateRaw();
		repo.save(RefreshToken.create(userId, sha256(raw), OffsetDateTime.now().plusDays(refreshTtlDays)));
		return raw;
	}

	/**
	 * Ham token'ı doğrular, iptal eder (rotasyon) ve kullanıcı id'sini döndürür.
	 */
	@Transactional
	public UUID consume(String rawToken) {
		if (rawToken == null || rawToken.isBlank()) {
			throw new InvalidRefreshTokenException();
		}
		RefreshToken token = repo.findByTokenHash(sha256(rawToken)).orElseThrow(InvalidRefreshTokenException::new);
		if (token.isRevoked() || token.getExpiresAt().isBefore(OffsetDateTime.now())) {
			throw new InvalidRefreshTokenException();
		}
		token.revoke();
		return token.getUserId();
	}

	private String generateRaw() {
		byte[] bytes = new byte[32];
		random.nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	private String sha256(String value) {
		try {
			byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(digest);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}
}