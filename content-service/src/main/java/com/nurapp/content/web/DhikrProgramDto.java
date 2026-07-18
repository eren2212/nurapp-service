package com.nurapp.content.web;

public record DhikrProgramDto(
        String key, String name, String description, String arabic, int defaultTarget, boolean premium) {
}
