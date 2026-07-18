package com.nurapp.user.web;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Bean Validation kısıtlarının UpdateProfileRequest/UpdatePreferencesRequest
 * üzerinde beklendiği gibi tetiklendiğini doğrular (Spring context'i gerekmez).
 */
class RequestValidationTest {

	private static ValidatorFactory factory;
	private static Validator validator;

	@BeforeAll
	static void setUp() {
		factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@AfterAll
	static void tearDown() {
		factory.close();
	}

	// --- UpdateProfileRequest ---------------------------------------------------

	@Test
	void profileNullFullNameIsValid_meansNoChange() {
		assertThat(validator.validate(new UpdateProfileRequest(null))).isEmpty();
	}

	@Test
	void profileBlankFullNameIsValid_meansClearName() {
		assertThat(validator.validate(new UpdateProfileRequest(""))).isEmpty();
	}

	@Test
	void profileNormalFullNameIsValid() {
		assertThat(validator.validate(new UpdateProfileRequest("Ahmet Yılmaz"))).isEmpty();
	}

	@Test
	void profileFullNameOver100CharsIsRejected() {
		String tooLong = "a".repeat(101);
		Set<ConstraintViolation<UpdateProfileRequest>> violations =
				validator.validate(new UpdateProfileRequest(tooLong));
		assertThat(violations).isNotEmpty();
	}

	@Test
	void profileFullNameAt100CharsIsAccepted() {
		String exact = "a".repeat(100);
		assertThat(validator.validate(new UpdateProfileRequest(exact))).isEmpty();
	}

	// --- UpdatePreferencesRequest -------------------------------------------------

	@Test
	void preferencesAllNullFieldsAreValid_meansNoChange() {
		assertThat(validator.validate(new UpdatePreferencesRequest(null, null, null, null, null))).isEmpty();
	}

	@Test
	void preferencesKnownValuesAreValid() {
		assertThat(validator.validate(
				new UpdatePreferencesRequest("tr", "DIYANET", "hanafi", "dark", true))).isEmpty();
	}

	@Test
	void preferencesRejectsGarbageLanguage() {
		Set<ConstraintViolation<UpdatePreferencesRequest>> violations = validator.validate(
				new UpdatePreferencesRequest("'; DROP TABLE users; --", null, null, null, null));
		assertThat(violations).isNotEmpty();
	}

	@Test
	void preferencesRejectsOverlyLongCalculationMethod() {
		Set<ConstraintViolation<UpdatePreferencesRequest>> violations = validator.validate(
				new UpdatePreferencesRequest(null, "a".repeat(21), null, null, null));
		assertThat(violations).isNotEmpty();
	}

	@Test
	void preferencesRejectsSingleCharacterValue_belowMinLength() {
		Set<ConstraintViolation<UpdatePreferencesRequest>> violations = validator.validate(
				new UpdatePreferencesRequest(null, null, "a", null, null));
		assertThat(violations).isNotEmpty();
	}

	@Test
	void preferencesRejectsWhitespaceOrSpecialCharacters() {
		Set<ConstraintViolation<UpdatePreferencesRequest>> violations = validator.validate(
				new UpdatePreferencesRequest(null, null, null, "dark theme!", null));
		assertThat(violations).isNotEmpty();
	}
}
