package com.nurapp.user.auth;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final DeviceAuthService deviceAuthService;
    private final EmailAuthService emailAuthService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(DeviceAuthService deviceAuthService, EmailAuthService emailAuthService,
                          JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.deviceAuthService = deviceAuthService;
        this.emailAuthService = emailAuthService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/device")
    public DeviceRegisterResponse device(@RequestBody(required = false) DeviceRegisterRequest req) {
        String deviceId = (req == null) ? null : req.deviceId();
        return deviceAuthService.registerDevice(deviceId);
    }

    @PostMapping("/token/refresh")
    public TokenPairResponse refresh(@RequestBody TokenRefreshRequest req) {
        var userId = refreshTokenService.consume(req.refreshToken());   // eskiyi iptal et
        return new TokenPairResponse(
                jwtService.issueAccessToken(userId),
                refreshTokenService.issue(userId));                     // yenisini ver
    }

    /** Korumalı: mevcut anonim kullanıcıyı e-posta/şifre ile hesaba yükseltir. */
    @PostMapping("/upgrade")
    public ResponseEntity<Void> upgrade(@AuthenticationPrincipal UUID userId,
                                        @Valid @RequestBody UpgradeRequest req) {
        emailAuthService.upgrade(userId, req.email(), req.password());
        return ResponseEntity.noContent().build();
    }

    /** Public: e-posta/şifre ile giriş. */
    @PostMapping("/login")
    public TokenPairResponse login(@Valid @RequestBody LoginRequest req) {
        return emailAuthService.login(req.email(), req.password());
    }
}
