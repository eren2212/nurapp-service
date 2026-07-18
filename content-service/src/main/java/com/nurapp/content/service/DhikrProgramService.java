package com.nurapp.content.service;

import com.nurapp.content.domain.DhikrProgramRepository;
import com.nurapp.content.domain.DhikrProgramTranslation;
import com.nurapp.content.domain.DhikrProgramTranslationRepository;
import com.nurapp.content.web.DhikrProgramDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DhikrProgramService {

    private static final String FALLBACK_LOCALE = "en";

    private final DhikrProgramRepository programs;
    private final DhikrProgramTranslationRepository translations;

    public DhikrProgramService(DhikrProgramRepository programs,
                               DhikrProgramTranslationRepository translations) {
        this.programs = programs;
        this.translations = translations;
    }

    @Transactional(readOnly = true)
    public List<DhikrProgramDto> list(String locale) {
        Map<UUID, DhikrProgramTranslation> requested = byProgramId(translations.findByLocale(locale));
        Map<UUID, DhikrProgramTranslation> fallback = byProgramId(translations.findByLocale(FALLBACK_LOCALE));

        return programs.findAllByOrderBySortOrderAsc().stream()
                .map(p -> {
                    var t = requested.getOrDefault(p.getId(), fallback.get(p.getId()));
                    String name = (t != null) ? t.getName() : p.getKey();
                    String description = (t != null) ? t.getDescription() : null;
                    return new DhikrProgramDto(
                            p.getKey(), name, description, p.getArabic(), p.getDefaultTarget(), p.isPremium());
                })
                .toList();
    }

    private Map<UUID, DhikrProgramTranslation> byProgramId(List<DhikrProgramTranslation> list) {
        return list.stream().collect(Collectors.toMap(
                DhikrProgramTranslation::getProgramId, Function.identity(), (a, b) -> a));
    }
}
