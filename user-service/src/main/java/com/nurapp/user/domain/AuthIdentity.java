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
@Table(name = "auth_identities")
public class AuthIdentity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@Column(nullable = false)
	private String provider;

	@Column(name = "provider_uid", nullable = false)
	private String providerUid;

	private String email;

	@Column(name = "password_hash")
	private String passwordHash;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private OffsetDateTime createdAt;

	protected AuthIdentity() {
	}

	/** Anonim cihaz kimliği oluşturur. */
	public static AuthIdentity device(UUID userId, String deviceId) {
		AuthIdentity a = new AuthIdentity();
		a.userId = userId;
		a.provider = "device";
		a.providerUid = deviceId;
		return a;
	}

	/** E-posta/şifre kimliği oluşturur (provider_uid = normalize edilmiş e-posta). */
	public static AuthIdentity email(UUID userId, String email, String passwordHash) {
		AuthIdentity a = new AuthIdentity();
		a.userId = userId;
		a.provider = "email";
		a.providerUid = email;
		a.email = email;
		a.passwordHash = passwordHash;
		return a;
	}

	public UUID getId() {
		return id;
	}

	public UUID getUserId() {
		return userId;
	}

	public String getProvider() {
		return provider;
	}

	public String getProviderUid() {
		return providerUid;
	}

	public String getEmail() {
		return email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}
}