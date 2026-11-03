package com.polarbookshop.catalogservice.domain;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IsbnValidatorTests {

  private static Validator validator;

  record IsbnHolder(@ValidIsbn String isbn) {}

  @BeforeAll
  static void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void whenIsbn10CheckDigitCorrectThenValidationSucceeds() {
    Set<ConstraintViolation<IsbnHolder>> violations = validator.validate(new IsbnHolder("0306406152"));
    assertThat(violations).isEmpty();
  }

  @Test
  void whenIsbn10CheckDigitWrongThenValidationFails() {
    Set<ConstraintViolation<IsbnHolder>> violations = validator.validate(new IsbnHolder("0306406153"));
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .isEqualTo("The ISBN check digit is not valid.");
  }

  @Test
  void whenIsbn13CheckDigitCorrectThenValidationSucceeds() {
    Set<ConstraintViolation<IsbnHolder>> violations = validator.validate(new IsbnHolder("9780306406157"));
    assertThat(violations).isEmpty();
  }

  @Test
  void whenIsbn13CheckDigitWrongThenValidationFails() {
    Set<ConstraintViolation<IsbnHolder>> violations = validator.validate(new IsbnHolder("9780306406158"));
    assertThat(violations).hasSize(1);
  }

  @Test
  void whenHandoutExampleIsbnThenValidationFails() {
    Set<ConstraintViolation<IsbnHolder>> violations = validator.validate(new IsbnHolder("1234567890"));
    assertThat(violations).hasSize(1);
  }

  @Test
  void whenIsbnMissingOrWrongLengthThenLeftToOtherConstraints() {
    assertThat(validator.validate(new IsbnHolder(null))).isEmpty();
    assertThat(validator.validate(new IsbnHolder("12345"))).isEmpty();
  }
}
