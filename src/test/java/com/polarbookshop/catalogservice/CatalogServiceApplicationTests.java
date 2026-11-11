package com.polarbookshop.catalogservice;

import com.polarbookshop.catalogservice.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CatalogServiceApplicationTests {

  @Autowired
  private WebTestClient webTestClient;

  @Test
  void whenPostRequestThenBookCreated() {
    var expected = Book.of("1231231231", "Title", "Author", 9.90);

    webTestClient
        .post().uri("/books")
        .bodyValue(expected)
        .exchange()
        .expectStatus().isCreated()
        .expectBody(Book.class).value(actual -> {
          assertThat(actual).isNotNull();
          assertThat(actual.isbn()).isEqualTo(expected.isbn());
        });
  }

  @Test
  void whenPostGetPutDeleteThenBookIsGone() {
    var isbn = "9876543210";
    var book = Book.of(isbn, "Title", "Author", 9.90);

    webTestClient.post().uri("/books").bodyValue(book)
        .exchange()
        .expectStatus().isCreated();

    webTestClient.get().uri("/books/" + isbn)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Book.class).value(actual -> {
          assertThat(actual.isbn()).isEqualTo(isbn);
          assertThat(actual.title()).isEqualTo(book.title());
        });

    var updated = Book.of(isbn, "New Title", "Author", 12.50);
    webTestClient.put().uri("/books/" + isbn).bodyValue(updated)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Book.class).value(actual -> {
          assertThat(actual.title()).isEqualTo(updated.title());
          assertThat(actual.price()).isEqualTo(updated.price());
        });

    webTestClient.delete().uri("/books/" + isbn)
        .exchange()
        .expectStatus().isNoContent();

    webTestClient.get().uri("/books/" + isbn)
        .exchange()
        .expectStatus().isNotFound();
  }
}
