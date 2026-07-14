package com.nurapp.user.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class InfoController {

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of("service", "user-service", "time", OffsetDateTime.now());
    }
}
