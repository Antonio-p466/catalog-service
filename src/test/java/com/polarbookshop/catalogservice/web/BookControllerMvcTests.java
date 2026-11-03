package com.polarbookshop.catalogservice.web;

import com.polarbookshop.catalogservice.domain.Book;
import com.polarbookshop.catalogservice.domain.BookAlreadyExistsException;
import com.polarbookshop.catalogservice.domain.BookNotFoundException;
import com.polarbookshop.catalogservice.domain.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerMvcTests {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private BookService bookService;

  @Test
  void whenGetBookNotExistingThenShouldReturn404() throws Exception {
    String isbn = "7373731394";
    given(bookService.viewBookDetails(isbn))
        .willThrow(BookNotFoundException.class);

    mockMvc.perform(get("/books/" + isbn))
        .andExpect(status().isNotFound());
  }

  @Test
  void whenPostBookAlreadyExistingThenShouldReturn422() throws Exception {
    String isbn = "7373731394";
    given(bookService.addBookToCatalog(any()))
        .willThrow(new BookAlreadyExistsException(isbn));

    mockMvc.perform(post("/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"isbn": "7373731394", "title": "Title", "author": "Author", "price": 9.90}
                """))
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  void whenPostBookInvalidThenShouldReturn400() throws Exception {
    mockMvc.perform(post("/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"isbn": "123ABC456Z", "title": "", "author": "Author", "price": 9.90}
                """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.isbn").value("The ISBN format must be valid."))
        .andExpect(jsonPath("$.title").value("The book title must be defined."));

    verify(bookService, never()).addBookToCatalog(any());
  }

  @Test
  void whenPutBookExistingThenShouldReturn200() throws Exception {
    String isbn = "7373731394";
    var book = new Book(isbn, "Title", "Author", 12.50);
    given(bookService.isInCatalog(isbn)).willReturn(true);
    given(bookService.editBookDetails(any(), any())).willReturn(book);

    mockMvc.perform(put("/books/" + isbn)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"isbn": "7373731394", "title": "Title", "author": "Author", "price": 12.50}
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.price").value(12.50));
  }

  @Test
  void whenPutBookNotExistingThenShouldReturn201() throws Exception {
    String isbn = "7373731394";
    var book = new Book(isbn, "Title", "Author", 9.90);
    given(bookService.isInCatalog(isbn)).willReturn(false);
    given(bookService.editBookDetails(any(), any())).willReturn(book);

    mockMvc.perform(put("/books/" + isbn)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"isbn": "7373731394", "title": "Title", "author": "Author", "price": 9.90}
                """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.isbn").value(isbn));
  }

  @Test
  void whenPutIsbnInPathAndBodyDifferentThenShouldReturn400() throws Exception {
    mockMvc.perform(put("/books/1111111111")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"isbn": "2222222222", "title": "Title", "author": "Author", "price": 9.90}
                """))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("The ISBN in the path (1111111111) and in the body (2222222222) must match."));

    verify(bookService, never()).editBookDetails(any(), any());
  }
}
