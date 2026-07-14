package com.nurapp.content.web;

public record DhikrProgramDto(
        String key, String name, String description, int defaultTarget, boolean premium) {
}
