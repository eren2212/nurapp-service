package com.nurapp.subscription.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "entitlements")
public class Entitlement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 64)
    private String entitlement;

    @Column(nullable = false)
    private boolean active = false;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected Entitlement() {
    }

    public static Entitlement create(UUID userId, String entitlement) {
        Entitlement e = new Entitlement();
        e.userId = userId;
        e.entitlement = entitlement;
        return e;
    }

    public void update(boolean active, OffsetDateTime expiresAt) {
        this.active = active;
        this.expiresAt = expiresAt;
    }

    public boolean isActive() {
        return active;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    /** Aktif ve süresi dolmamış mı? */
    public boolean isCurrentlyActive() {
        return active && (expiresAt == null || expiresAt.isAfter(OffsetDateTime.now()));
    }
}
