package com.polarbookshop.catalogservice.web;

import com.polarbookshop.catalogservice.domain.Book;
import com.polarbookshop.catalogservice.domain.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("books")
public class BookController {

  private final BookService bookService;

  public BookController(BookService bookService) {
    this.bookService = bookService;
  }

  @GetMapping
  public Iterable<Book> get() {
    return bookService.viewBookList();
  }

  @GetMapping("{isbn}")
  public Book getByIsbn(@PathVariable String isbn) {
    return bookService.viewBookDetails(isbn);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Book post(@Valid @RequestBody Book book) {
    return bookService.addBookToCatalog(book);
  }

  @DeleteMapping("{isbn}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable String isbn) {
    bookService.removeBookFromCatalog(isbn);
  }

  @PutMapping("{isbn}")
  public ResponseEntity<Book> put(@PathVariable String isbn, @Valid @RequestBody Book book) {
    if (!isbn.equals(book.isbn())) {
      throw new IsbnMismatchException(isbn, book.isbn());
    }
    boolean exists = bookService.isInCatalog(isbn);
    Book saved = bookService.editBookDetails(isbn, book);
    return ResponseEntity.status(exists ? HttpStatus.OK : HttpStatus.CREATED).body(saved);
  }
}
