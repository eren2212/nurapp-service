package com.nurapp.user.web;

import com.nurapp.user.domain.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository users;
    private final UserProfileService profileService;

    public UserController(UserRepository users, UserProfileService profileService) {
        this.users = users;
        this.profileService = profileService;
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(@AuthenticationPrincipal UUID userId) {
        return users.findById(userId)
                .map(u -> new MeResponse(u.getId(), u.getStatus(), u.getFullName(), u.getCreatedAt()))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/me")
    public MeResponse updateProfile(@AuthenticationPrincipal UUID userId,
                                     @RequestBody UpdateProfileRequest request) {
        return profileService.updateProfile(userId, request);
    }

    @GetMapping("/me/preferences")
    public PreferencesResponse getPreferences(@AuthenticationPrincipal UUID userId) {
        return profileService.getPreferences(userId);
    }

    @PatchMapping("/me/preferences")
    public PreferencesResponse updatePreferences(@AuthenticationPrincipal UUID userId,
                                                 @RequestBody UpdatePreferencesRequest request) {
        return profileService.updatePreferences(userId, request);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal UUID userId) {
        profileService.deleteAccount(userId);
        return ResponseEntity.noContent().build();
    }
}
