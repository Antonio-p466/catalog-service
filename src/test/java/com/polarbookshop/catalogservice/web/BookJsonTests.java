package com.polarbookshop.catalogservice.web;

import java.time.Instant;

import com.polarbookshop.catalogservice.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookJsonTests {

  @Autowired
  private JacksonTester<Book> json;

  @Test
  void testSerialize() throws Exception {
    var now = Instant.now();
    var book = new Book(394L, "1234567890", "Title", "Author", 9.90, now, now, 21);
    var content = json.write(book);
    assertThat(content).extractingJsonPathNumberValue("@.id").isEqualTo(book.id().intValue());
    assertThat(content).extractingJsonPathStringValue("@.isbn").isEqualTo(book.isbn());
    assertThat(content).extractingJsonPathStringValue("@.title").isEqualTo(book.title());
    assertThat(content).extractingJsonPathStringValue("@.author").isEqualTo(book.author());
    assertThat(content).extractingJsonPathNumberValue("@.price").isEqualTo(book.price());
    assertThat(content).extractingJsonPathStringValue("@.createdDate")
        .isEqualTo(now.toString());
    assertThat(content).extractingJsonPathStringValue("@.lastModifiedDate")
        .isEqualTo(now.toString());
    assertThat(content).extractingJsonPathNumberValue("@.version").isEqualTo(book.version());
  }

  @Test
  void testDeserialize() throws Exception {
    var instant = Instant.parse("2026-10-01T10:15:30.000Z");
    var content = """
        {
          "id": 394,
          "isbn": "1234567890",
          "title": "Title",
          "author": "Author",
          "price": 9.90,
          "createdDate": "2026-10-01T10:15:30.000Z",
          "lastModifiedDate": "2026-10-01T10:15:30.000Z",
          "version": 21
        }
        """;
    assertThat(json.parse(content))
        .usingRecursiveComparison()
        .isEqualTo(new Book(394L, "1234567890", "Title", "Author", 9.90,
            instant, instant, 21));
  }
}
