package com.nurapp.content.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "dhikr_programs")
public class DhikrProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 64)
    private String key;

    @Column(name = "default_target", nullable = false)
    private int defaultTarget;

    @Column(nullable = false)
    private boolean premium = false;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    protected DhikrProgram() {
    }

    public UUID getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public int getDefaultTarget() {
        return defaultTarget;
    }

    public boolean isPremium() {
        return premium;
    }
}
