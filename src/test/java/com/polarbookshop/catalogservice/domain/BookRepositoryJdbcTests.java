package com.polarbookshop.catalogservice.domain;

import java.util.Optional;
import java.util.stream.StreamSupport;

import com.polarbookshop.catalogservice.config.DataConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@Import(DataConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("integration")
class BookRepositoryJdbcTests {

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private JdbcAggregateTemplate jdbcAggregateTemplate;

  @Test
  void findBookByIsbnWhenExisting() {
    var isbn = "1234561237";
    var book = Book.of(isbn, "Title", "Author", 12.90, "Polarsophia");
    jdbcAggregateTemplate.insert(book);

    Optional<Book> actual = bookRepository.findByIsbn(isbn);

    assertThat(actual).isPresent();
    assertThat(actual.get().isbn()).isEqualTo(isbn);
  }

  @Test
  void findBookByIsbnWhenNotExisting() {
    assertThat(bookRepository.findByIsbn("1234561238")).isEmpty();
  }

  @Test
  void findAllBooks() {
    jdbcAggregateTemplate.insert(Book.of("1234561235", "Title", "Author", 12.90, "Polarsophia"));
    jdbcAggregateTemplate.insert(Book.of("1234561236", "Another Title", "Author", 9.90, "Polarsophia"));

    Iterable<Book> actual = bookRepository.findAll();

    assertThat(StreamSupport.stream(actual.spliterator(), false).map(Book::isbn))
        .contains("1234561235", "1234561236");
  }

  @Test
  void existsByIsbnWhenExisting() {
    jdbcAggregateTemplate.insert(Book.of("1234561239", "Title", "Author", 12.90, "Polarsophia"));

    assertThat(bookRepository.existsByIsbn("1234561239")).isTrue();
  }

  @Test
  void existsByIsbnWhenNotExisting() {
    assertThat(bookRepository.existsByIsbn("1234561240")).isFalse();
  }

  @Test
  void deleteByIsbn() {
    var isbn = "1234561241";
    jdbcAggregateTemplate.insert(Book.of(isbn, "Title", "Author", 12.90, "Polarsophia"));

    bookRepository.deleteByIsbn(isbn);

    assertThat(bookRepository.findByIsbn(isbn)).isEmpty();
  }

  @Test
  void createdDateIsSetWhenSaved() {
    var saved = bookRepository.save(Book.of("1234561242", "Title", "Author", 12.90, "Polarsophia"));

    assertThat(saved.createdDate()).isNotNull();
    assertThat(saved.lastModifiedDate()).isNotNull();
  }

  @Test
  void publisherIsSavedWhenGiven() {
    bookRepository.save(Book.of("1234561243", "Title", "Author", 12.90, "Polarsophia"));

    assertThat(bookRepository.findByIsbn("1234561243").get().publisher()).isEqualTo("Polarsophia");
  }

  @Test
  void publisherCanBeMissing() {
    bookRepository.save(Book.of("1234561244", "Title", "Author", 12.90, null));

    assertThat(bookRepository.findByIsbn("1234561244").get().publisher()).isNull();
  }

  @Test
  void findAllByAuthorReturnsOnlyThatAuthor() {
    jdbcAggregateTemplate.insert(Book.of("1234561245", "Northern Lights", "Lyra Silverstar", 9.90, "Polarsophia"));
    jdbcAggregateTemplate.insert(Book.of("1234561246", "Arctic Nights", "Lyra Silverstar", 14.50, "Polarsophia"));
    jdbcAggregateTemplate.insert(Book.of("1234561247", "Polar Journey", "Iorek Polarson", 12.90, "Polarsophia"));

    Iterable<Book> actual = bookRepository.findAllByAuthor("Lyra Silverstar");

    assertThat(StreamSupport.stream(actual.spliterator(), false).map(Book::isbn))
        .containsExactlyInAnyOrder("1234561245", "1234561246");
  }

  @Test
  void findAllByAuthorWhenNoneMatch() {
    assertThat(bookRepository.findAllByAuthor("Nobody")).isEmpty();
  }
}
