package com.nurapp.user.domain;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "service_meta")
public class ServiceMeta {

	@Id
	private Short id;

	@Column(name = "service_name", nullable = false)
	private String serviceName;

	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt;

	protected ServiceMeta() {
	}

	public Short getId() {
		return id;
	}

	public String getServiceName() {
		return serviceName;
	}

	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}
}