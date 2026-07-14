package com.nurapp.subscription.web;

import com.nurapp.subscription.service.SubscriptionService;
import com.nurapp.subscription.webhook.WebhookAuthenticator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final SubscriptionService service;
    private final WebhookAuthenticator webhookAuthenticator;

    public SubscriptionController(SubscriptionService service, WebhookAuthenticator webhookAuthenticator) {
        this.service = service;
        this.webhookAuthenticator = webhookAuthenticator;
    }

    /** RevenueCat tarafından çağrılır (public). Authorization gizli değeriyle doğrulanır. */
    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @RequestBody RevenueCatWebhook body) {
        if (!webhookAuthenticator.isValid(authorization)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        service.handleWebhook(body.event());
        return ResponseEntity.ok().build();
    }

    /** Kullanıcının premium durumu. Kimlik gateway'den X-User-Id başlığıyla gelir. */
    @GetMapping("/me")
    public EntitlementStatus me(@RequestHeader("X-User-Id") UUID userId) {
        return service.getStatus(userId);
    }
}
