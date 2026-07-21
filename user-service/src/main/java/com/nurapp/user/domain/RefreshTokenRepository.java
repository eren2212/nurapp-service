package com.nurapp.user.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
	Optional<RefreshToken> findByTokenHash(String tokenHash);

	/**
	 * Token'ı yalnızca hâlâ aktifken (revoked=false) atomik olarak iptal eder ve etkilenen
	 * satır sayısını döndürür. İki eşzamanlı tüketim yarışında DB satır kilidi çağrıları
	 * sıralar; ilkine 1, ikincisine 0 döner — böylece aynı refresh token iki kez kullanılamaz.
	 */
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("update RefreshToken t set t.revoked = true where t.tokenHash = :hash and t.revoked = false")
	int revokeIfActive(@Param("hash") String hash);
}