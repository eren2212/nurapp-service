package com.nurapp.content.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "dhikr_program_translations")
public class DhikrProgramTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "program_id", nullable = false)
    private UUID programId;

    @Column(nullable = false, length = 8)
    private String locale;

    @Column(nullable = false)
    private String name;

    private String description;

    protected DhikrProgramTranslation() {
    }

    public UUID getProgramId() {
        return programId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
