package com.nurapp.user.web;

/**
 * Kısmi güncelleme (PATCH): sadece null olmayan alanlar değiştirilir.
 */
public record UpdatePreferencesRequest(
        String language,
        String calculationMethod,
        String madhab,
        String theme,
        Boolean notificationsEnabled) {
}
