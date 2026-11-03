package com.polarbookshop.catalogservice.domain;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IsbnValidator implements ConstraintValidator<ValidIsbn, String> {

  @Override
  public boolean isValid(String isbn, ConstraintValidatorContext context) {
    if (isbn == null || !isbn.matches("^([0-9]{10}|[0-9]{13})$")) {
      return true;
    }
    return isbn.length() == 10 ? isValidIsbn10(isbn) : isValidIsbn13(isbn);
  }

  private boolean isValidIsbn10(String isbn) {
    int sum = 0;
    for (int i = 0; i < 10; i++) {
      sum += (10 - i) * Character.getNumericValue(isbn.charAt(i));
    }
    return sum % 11 == 0;
  }

  private boolean isValidIsbn13(String isbn) {
    int sum = 0;
    for (int i = 0; i < 13; i++) {
      sum += (i % 2 == 0 ? 1 : 3) * Character.getNumericValue(isbn.charAt(i));
    }
    return sum % 10 == 0;
  }
}
