package com.nurapp.user.web;

import com.nurapp.user.domain.UserPreferences;
import com.nurapp.user.domain.UserPreferencesRepository;
import com.nurapp.user.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserProfileService {

    private final UserRepository users;
    private final UserPreferencesRepository preferences;

    public UserProfileService(UserRepository users, UserPreferencesRepository preferences) {
        this.users = users;
        this.preferences = preferences;
    }

    @Transactional(readOnly = true)
    public PreferencesResponse getPreferences(UUID userId) {
        return preferences.findById(userId)
                .map(this::toResponse)
                .orElseGet(() -> toResponse(UserPreferences.defaults(userId)));
    }

    @Transactional
    public PreferencesResponse updatePreferences(UUID userId, UpdatePreferencesRequest req) {
        UserPreferences prefs = preferences.findById(userId)
                .orElseGet(() -> UserPreferences.defaults(userId));

        if (req.language() != null) {
            prefs.setLanguage(req.language());
        }
        if (req.calculationMethod() != null) {
            prefs.setCalculationMethod(req.calculationMethod());
        }
        if (req.madhab() != null) {
            prefs.setMadhab(req.madhab());
        }
        if (req.theme() != null) {
            prefs.setTheme(req.theme());
        }
        if (req.notificationsEnabled() != null) {
            prefs.setNotificationsEnabled(req.notificationsEnabled());
        }

        return toResponse(preferences.save(prefs));
    }

    /**
     * Hesabı ve tüm user-service verisini siler (auth_identities, refresh_tokens,
     * user_preferences FK ON DELETE CASCADE ile birlikte gider).
     * Not: diğer servislerdeki veri (ör. subscription entitlements) ileride
     * "kullanıcı silindi" olayıyla temizlenecek.
     */
    @Transactional
    public void deleteAccount(UUID userId) {
        if (users.existsById(userId)) {
            users.deleteById(userId);
        }
    }

    private PreferencesResponse toResponse(UserPreferences p) {
        return new PreferencesResponse(
                p.getLanguage(), p.getCalculationMethod(), p.getMadhab(),
                p.getTheme(), p.isNotificationsEnabled());
    }
}
