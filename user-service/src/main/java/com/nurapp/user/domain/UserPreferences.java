package com.nurapp.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_preferences")
public class UserPreferences {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false, length = 8)
    private String language;

    @Column(name = "calculation_method", nullable = false, length = 32)
    private String calculationMethod;

    @Column(nullable = false, length = 16)
    private String madhab;

    @Column(nullable = false, length = 16)
    private String theme;

    @Column(name = "notifications_enabled", nullable = false)
    private boolean notificationsEnabled;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected UserPreferences() {
    }

    /** Kaydı olmayan kullanıcı için varsayılan tercihler (kaydedilmeden döndürülebilir). */
    public static UserPreferences defaults(UUID userId) {
        UserPreferences p = new UserPreferences();
        p.userId = userId;
        p.language = "en";
        p.calculationMethod = "MWL";
        p.madhab = "shafi";
        p.theme = "system";
        p.notificationsEnabled = true;
        return p;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getLanguage() {
        return language;
    }

    public String getCalculationMethod() {
        return calculationMethod;
    }

    public String getMadhab() {
        return madhab;
    }

    public String getTheme() {
        return theme;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setCalculationMethod(String calculationMethod) {
        this.calculationMethod = calculationMethod;
    }

    public void setMadhab(String madhab) {
        this.madhab = madhab;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }
}
