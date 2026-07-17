package com.nurapp.user.web;

/**
 * Kısmi güncelleme (PATCH): sadece null olmayan alanlar değiştirilir.
 */
public record UpdateProfileRequest(String fullName) {
}
