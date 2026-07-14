package com.nurapp.user.web;

import java.time.OffsetDateTime;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nurapp.user.domain.ServiceMeta;
import com.nurapp.user.domain.ServiceMetaRepository;

@RestController
@RequestMapping("/users")
public class InfoController {

	private final ServiceMetaRepository repo;

	public InfoController(ServiceMetaRepository repo) {
		this.repo = repo;
	}

	@GetMapping("/ping")
	public Map<String, Object> ping() {
		return Map.of("service", "user-service", "time", OffsetDateTime.now());
	}

	@GetMapping("/info")
	public Map<String, Object> info() {
		ServiceMeta meta = repo.findById((short) 1).orElseThrow();
		return Map.of("service", meta.getServiceName(), "dbRowCreatedAt", meta.getCreatedAt(), "now",
				OffsetDateTime.now());
	}
}