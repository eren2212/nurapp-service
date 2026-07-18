package com.nurapp.user.web;

import jakarta.validation.constraints.Pattern;

/**
 * Kısmi güncelleme (PATCH): sadece null olmayan alanlar değiştirilir.
 * Backend bu kodları serbest string olarak saklar (UI etiketi eşlemesi mobilde,
 * preferences-options.ts'te) — bu yüzden tam bir enum yerine, çöp/aşırı uzun
 * değerleri engelleyen gevşek bir desen kontrolü yeterli.
 */
public record UpdatePreferencesRequest(
        @Pattern(regexp = "^[a-zA-Z_]{2,20}$") String language,
        @Pattern(regexp = "^[a-zA-Z_]{2,20}$") String calculationMethod,
        @Pattern(regexp = "^[a-zA-Z_]{2,20}$") String madhab,
        @Pattern(regexp = "^[a-zA-Z_]{2,20}$") String theme,
        Boolean notificationsEnabled) {
}
