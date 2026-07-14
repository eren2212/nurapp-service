package com.nurapp.content.web;

import com.nurapp.content.service.DhikrProgramService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {

    private final DhikrProgramService service;

    public ContentController(DhikrProgramService service) {
        this.service = service;
    }

    @GetMapping("/dhikr-programs")
    public List<DhikrProgramDto> dhikrPrograms(@RequestParam(defaultValue = "en") String locale) {
        return service.list(locale);
    }
}
