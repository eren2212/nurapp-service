package com.nurapp.subscription.web;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * RevenueCat webhook gövdesi (ilgili alanlar). Bilinmeyen alanlar yok sayılır (Spring varsayılanı).
 * Bkz: RevenueCat "event" objesi.
 */
public record RevenueCatWebhook(Event event) {

    public record Event(
            String id,
            String type,
            @JsonProperty("app_user_id") String appUserId,
            @JsonProperty("entitlement_ids") List<String> entitlementIds,
            @JsonProperty("expiration_at_ms") Long expirationAtMs,
            @JsonProperty("product_id") String productId,
            String store
    ) {
    }
}
