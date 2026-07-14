package com.nurapp.user.web;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nurapp.user.domain.UserRepository;

@RestController
@RequestMapping("/users")
public class UserController {

	private final UserRepository users;

	public UserController(UserRepository users) {
		this.users = users;
	}

	@GetMapping("/me")
	public ResponseEntity<Object> me(@AuthenticationPrincipal UUID userId) {
		return users.findById(userId)
				.<Object>map(u -> Map.of("userId", u.getId(), "status", u.getStatus(), "createdAt", u.getCreatedAt()))
				.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}
}