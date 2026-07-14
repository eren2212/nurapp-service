package com.nurapp.user.web;

public record PreferencesResponse(
        String language,
        String calculationMethod,
        String madhab,
        String theme,
        boolean notificationsEnabled) {
}
