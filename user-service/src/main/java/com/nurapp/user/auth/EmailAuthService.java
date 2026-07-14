package com.nurapp.user.auth;

import com.nurapp.user.domain.AuthIdentity;
import com.nurapp.user.domain.AuthIdentityRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class EmailAuthService {

    private static final String PROVIDER = "email";

    private final AuthIdentityRepository identities;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwt;
    private final RefreshTokenService refreshTokens;

    public EmailAuthService(AuthIdentityRepository identities, PasswordEncoder passwordEncoder,
                            JwtService jwt, RefreshTokenService refreshTokens) {
        this.identities = identities;
        this.passwordEncoder = passwordEncoder;
        this.jwt = jwt;
        this.refreshTokens = refreshTokens;
    }

    /** Mevcut (anonim) kullanıcıya e-posta/şifre kimliği bağlar. */
    @Transactional
    public void upgrade(UUID userId, String email, String password) {
        String normalized = normalize(email);
        if (identities.findByProviderAndProviderUid(PROVIDER, normalized).isPresent()) {
            throw new EmailAlreadyInUseException();
        }
        identities.save(AuthIdentity.email(userId, normalized, passwordEncoder.encode(password)));
    }

    /** E-posta/şifre ile giriş; başarılıysa token çifti döndürür. */
    @Transactional
    public TokenPairResponse login(String email, String password) {
        AuthIdentity identity = identities.findByProviderAndProviderUid(PROVIDER, normalize(email))
                .orElseThrow(InvalidCredentialsException::new);
        if (identity.getPasswordHash() == null
                || !passwordEncoder.matches(password, identity.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        UUID userId = identity.getUserId();
        return new TokenPairResponse(jwt.issueAccessToken(userId), refreshTokens.issue(userId));
    }

    private String normalize(String email) {
        return email.trim().toLowerCase();
    }
}
