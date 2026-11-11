package com.polarbookshop.catalogservice.domain;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookValidationTests {

  private static Validator validator;

  @BeforeAll
  static void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void whenAllFieldsCorrectThenValidationSucceeds() {
    var book = Book.of("1234567890", "Title", "Author", 9.90, "Polarsophia");
    Set<ConstraintViolation<Book>> violations = validator.validate(book);
    assertThat(violations).isEmpty();
  }

  @Test
  void whenIsbnDefinedButIncorrectThenValidationFails() {
    var book = Book.of("a234567890", "Title", "Author", 9.90, "Polarsophia");
    Set<ConstraintViolation<Book>> violations = validator.validate(book);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .isEqualTo("The ISBN format must be valid.");
  }

  @Test
  void whenTitleIsBlankThenValidationFails() {
    var book = Book.of("1234567890", "", "Author", 9.90, "Polarsophia");
    Set<ConstraintViolation<Book>> violations = validator.validate(book);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .isEqualTo("The book title must be defined.");
  }

  @Test
  void whenAuthorIsBlankThenValidationFails() {
    var book = Book.of("1234567890", "Title", "", 9.90, "Polarsophia");
    Set<ConstraintViolation<Book>> violations = validator.validate(book);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .isEqualTo("The book author must be defined.");
  }

  @Test
  void whenPriceIsMissingThenValidationFails() {
    var book = Book.of("1234567890", "Title", "Author", null, "Polarsophia");
    Set<ConstraintViolation<Book>> violations = validator.validate(book);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .isEqualTo("The book price must be defined.");
  }

  @Test
  void whenPriceIsZeroThenValidationFails() {
    var book = Book.of("1234567890", "Title", "Author", 0.0, "Polarsophia");
    Set<ConstraintViolation<Book>> violations = validator.validate(book);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .isEqualTo("The book price must be greater than zero.");
  }

  @Test
  void whenPriceIsNegativeThenValidationFails() {
    var book = Book.of("1234567890", "Title", "Author", -9.90, "Polarsophia");
    Set<ConstraintViolation<Book>> violations = validator.validate(book);
    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .isEqualTo("The book price must be greater than zero.");
  }
}
