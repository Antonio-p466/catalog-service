package com.polarbookshop.catalogservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.polarbookshop.catalogservice.domain.Book;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@Testcontainers
class CatalogServiceApplicationTests {

  @Container
  static KeycloakContainer keycloak =
      new KeycloakContainer("quay.io/keycloak/keycloak:26.0")
          .withRealmImportFile("test-realm-config.json");

  @DynamicPropertySource
  static void keycloakProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
        () -> keycloak.getAuthServerUrl() + "/realms/PolarBookshop");
  }

  private static String isabelleToken;
  private static String bjornToken;

  @BeforeAll
  static void generateAccessTokens() {
    var webClient = WebClient.builder()
        .baseUrl(keycloak.getAuthServerUrl() + "/realms/PolarBookshop/protocol/openid-connect/token")
        .build();
    isabelleToken = tokenFor("isabelle", webClient);
    bjornToken = tokenFor("bjorn", webClient);
  }

  private static String tokenFor(String username, WebClient webClient) {
    var response = webClient.post()
        .body(BodyInserters.fromFormData("grant_type", "password")
            .with("client_id", "polar-test")
            .with("username", username)
            .with("password", "password"))
        .retrieve()
        .bodyToMono(JsonNode.class)
        .block();
    return response.get("access_token").asText();
  }

  @Autowired
  private WebTestClient webTestClient;

  @Test
  void whenPostAsEmployeeThen201() {
    var expected = Book.of("1231231231", "Title", "Author", 9.90, "Polarsophia");

    webTestClient
        .post().uri("/books")
        .headers(headers -> headers.setBearerAuth(isabelleToken))
        .bodyValue(expected)
        .exchange()
        .expectStatus().isCreated()
        .expectBody(Book.class).value(actual -> {
          assertThat(actual).isNotNull();
          assertThat(actual.isbn()).isEqualTo(expected.isbn());
        });
  }

  @Test
  void whenPostAsCustomerThen403() {
    webTestClient
        .post().uri("/books")
        .headers(headers -> headers.setBearerAuth(bjornToken))
        .bodyValue(Book.of("1231231232", "Title", "Author", 9.90, "Polarsophia"))
        .exchange()
        .expectStatus().isForbidden();
  }

  @Test
  void whenPostUnauthenticatedThen401() {
    webTestClient
        .post().uri("/books")
        .bodyValue(Book.of("1231231233", "Title", "Author", 9.90, "Polarsophia"))
        .exchange()
        .expectStatus().isUnauthorized();
  }

  @Test
  void whenPostGetPutDeleteThenBookIsGone() {
    var isbn = "9876543210";
    var book = Book.of(isbn, "Title", "Author", 9.90, "Polarsophia");

    webTestClient.post().uri("/books")
        .headers(headers -> headers.setBearerAuth(isabelleToken))
        .bodyValue(book)
        .exchange()
        .expectStatus().isCreated();

    webTestClient.get().uri("/books/" + isbn)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Book.class).value(actual -> {
          assertThat(actual.isbn()).isEqualTo(isbn);
          assertThat(actual.title()).isEqualTo(book.title());
        });

    var updated = Book.of(isbn, "New Title", "Author", 12.50, "Polarsophia");
    webTestClient.put().uri("/books/" + isbn)
        .headers(headers -> headers.setBearerAuth(isabelleToken))
        .bodyValue(updated)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Book.class).value(actual -> {
          assertThat(actual.title()).isEqualTo(updated.title());
          assertThat(actual.price()).isEqualTo(updated.price());
        });

    webTestClient.delete().uri("/books/" + isbn)
        .headers(headers -> headers.setBearerAuth(isabelleToken))
        .exchange()
        .expectStatus().isNoContent();

    webTestClient.get().uri("/books/" + isbn)
        .exchange()
        .expectStatus().isNotFound();
  }
}
