package com.polarbookshop.catalogservice.web;

public class IsbnMismatchException extends RuntimeException {
  public IsbnMismatchException(String pathIsbn, String bodyIsbn) {
    super("The ISBN in the path (" + pathIsbn + ") and in the body (" + bodyIsbn + ") must match.");
  }
}
