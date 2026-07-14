package com.nurapp.content.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DhikrProgramRepository extends JpaRepository<DhikrProgram, UUID> {
    List<DhikrProgram> findAllByOrderBySortOrderAsc();
}
