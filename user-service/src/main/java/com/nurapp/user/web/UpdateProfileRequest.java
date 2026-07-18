package com.nurapp.user.web;

import jakarta.validation.constraints.Size;

/**
 * Kısmi güncelleme (PATCH): sadece null olmayan alanlar değiştirilir.
 * Boş string bilinçli olarak isim temizleme anlamına gelir (bkz. UserProfileService) —
 * bu yüzden @NotBlank değil, yalnızca üst sınır kontrolü var.
 */
public record UpdateProfileRequest(@Size(max = 100) String fullName) {
}
