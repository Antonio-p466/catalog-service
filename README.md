# Catalog Service

[![Commit Stage](https://github.com/Antonio-p466/catalog-service/actions/workflows/commit-stage.yml/badge.svg)](https://github.com/Antonio-p466/catalog-service/actions/workflows/commit-stage.yml)

Spring boot REST API for Polar Bookshop. It keeps the books in the catalog and lets you list them, look one up by isbn, and add, edit or delete them.
there is no database yet, the books are kept in memory so they are gone when the app stops. a real database comes in a later practicum.

## Team
- Antonio Lee

## Requirements
- Java 21
- Spring Boot 3.5.16. start.spring.io only has 4.x now, so i generated it there and set the version to the newest 3.5.x in build.gradle

## Build and test
```
./gradlew build
```
this also runs all the tests. the report is in build/reports/tests/test/index.html

## Run
```
./gradlew bootRun
```
Then http://localhost:9001/books

## How to use it
- GET /books lists every book
- POST /books adds a book. if the isbn is already there you get 422, if something in the book is wrong you get 400 with what's wrong
- GET /books/{isbn} gets one book, or 404 if there isnt one with that isbn
- PUT /books/{isbn} changes the book, and if it doesnt exist yet it makes it
- DELETE /books/{isbn} deletes the book and gives back 204

a book needs an isbn with 10 or 13 digits, a title, an author and a price more than 0.
docs/api-demo.md has every one of these tried with curl.

## Project structure
- com.polarbookshop.catalogservice.domain - Book, BookService, BookRepository and the two exceptions
- com.polarbookshop.catalogservice.persistence - InMemoryBookRepository
- com.polarbookshop.catalogservice.web - BookController and BookControllerAdvice
- src/test/java - validation unit tests, the full integration test, and the web and json slice tests

## Commit stage
.github/workflows/commit-stage.yml runs on every push. it builds the app, runs the tests, scans the dependencies with grype and puts the report in the Security tab.
