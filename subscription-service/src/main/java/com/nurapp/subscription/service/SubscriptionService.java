package com.nurapp.subscription.service;

import com.nurapp.subscription.domain.Entitlement;
import com.nurapp.subscription.domain.EntitlementRepository;
import com.nurapp.subscription.domain.WebhookEvent;
import com.nurapp.subscription.domain.WebhookEventRepository;
import com.nurapp.subscription.web.EntitlementStatus;
import com.nurapp.subscription.web.RevenueCatWebhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class SubscriptionService {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionService.class);
    private static final String DEFAULT_ENTITLEMENT = "premium";

    private final WebhookEventRepository events;
    private final EntitlementRepository entitlements;

    public SubscriptionService(WebhookEventRepository events, EntitlementRepository entitlements) {
        this.events = events;
        this.entitlements = entitlements;
    }

    @Transactional
    public void handleWebhook(RevenueCatWebhook.Event event) {
        if (event == null || event.id() == null || event.id().isBlank()) {
            log.warn("Geçersiz webhook: event ya da id yok");
            return;
        }
        // Idempotency: aynı olay iki kez gelirse tekrar işleme
        if (events.existsByEventId(event.id())) {
            log.info("Webhook zaten işlenmiş, atlanıyor: {}", event.id());
            return;
        }
        events.save(WebhookEvent.of(event.id(), event.type()));

        UUID userId = parseUuid(event.appUserId());
        if (userId == null) {
            log.warn("Webhook app_user_id UUID değil: {}", event.appUserId());
            return;
        }

        boolean active = isActiveType(event.type());
        OffsetDateTime expiresAt = toOffset(event.expirationAtMs());
        List<String> ents = (event.entitlementIds() != null && !event.entitlementIds().isEmpty())
                ? event.entitlementIds()
                : List.of(DEFAULT_ENTITLEMENT);

        for (String ent : ents) {
            upsert(userId, ent, active, expiresAt);
        }
        log.info("Entitlement güncellendi: user={} active={} type={}", userId, active, event.type());
    }

    @Transactional(readOnly = true)
    public EntitlementStatus getStatus(UUID userId) {
        return entitlements.findByUserIdAndEntitlement(userId, DEFAULT_ENTITLEMENT)
                .map(e -> new EntitlementStatus(e.isCurrentlyActive(), e.getExpiresAt()))
                .orElse(new EntitlementStatus(false, null));
    }

    private void upsert(UUID userId, String ent, boolean active, OffsetDateTime expiresAt) {
        Entitlement entity = entitlements.findByUserIdAndEntitlement(userId, ent)
                .orElseGet(() -> Entitlement.create(userId, ent));
        entity.update(active, expiresAt);
        entitlements.save(entity);
    }

    /** EXPIRATION dışındaki her olay tipi aktif sayılır (satın alma, yenileme, iptal-ama-süre-devam vb.). */
    private boolean isActiveType(String type) {
        return type != null && !type.equalsIgnoreCase("EXPIRATION");
    }

    private OffsetDateTime toOffset(Long epochMillis) {
        return (epochMillis == null) ? null : Instant.ofEpochMilli(epochMillis).atOffset(ZoneOffset.UTC);
    }

    private UUID parseUuid(String value) {
        try {
            return (value == null) ? null : UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
