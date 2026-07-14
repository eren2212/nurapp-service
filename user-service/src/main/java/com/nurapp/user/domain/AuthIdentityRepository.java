package com.nurapp.user.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthIdentityRepository extends JpaRepository<AuthIdentity, UUID> {
	Optional<AuthIdentity> findByProviderAndProviderUid(String provider, String providerUid);
}