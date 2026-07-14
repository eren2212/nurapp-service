package com.nurapp.user.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@Column(name = "token_hash", nullable = false, unique = true, length = 64)
	private String tokenHash;

	@Column(name = "expires_at", nullable = false)
	private OffsetDateTime expiresAt;

	@Column(nullable = false)
	private boolean revoked = false;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private OffsetDateTime createdAt;

	protected RefreshToken() {
	}

	public static RefreshToken create(UUID userId, String tokenHash, OffsetDateTime expiresAt) {
		RefreshToken t = new RefreshToken();
		t.userId = userId;
		t.tokenHash = tokenHash;
		t.expiresAt = expiresAt;
		return t;
	}

	public UUID getUserId() {
		return userId;
	}

	public OffsetDateTime getExpiresAt() {
		return expiresAt;
	}

	public boolean isRevoked() {
		return revoked;
	}

	public void revoke() {
		this.revoked = true;
	}
}