package com.nurapp.subscription.webhook;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * RevenueCat webhook doğrulaması: RevenueCat panelinde ayarlanan Authorization header
 * değeri, yapılandırdığımız gizli değerle sabit-zamanlı karşılaştırılır.
 */
@Component
public class WebhookAuthenticator {

    private final String secret;

    public WebhookAuthenticator(@Value("${app.revenuecat.webhook-secret}") String secret) {
        this.secret = secret;
    }

    public boolean isValid(String authorizationHeader) {
        if (secret == null || secret.isBlank() || authorizationHeader == null) {
            return false;
        }
        return MessageDigest.isEqual(
                secret.getBytes(StandardCharsets.UTF_8),
                authorizationHeader.getBytes(StandardCharsets.UTF_8));
    }
}
