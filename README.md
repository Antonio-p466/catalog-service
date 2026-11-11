# Catalog Service

[![Commit Stage](https://github.com/Antonio-p466/catalog-service/actions/workflows/commit-stage.yml/badge.svg)](https://github.com/Antonio-p466/catalog-service/actions/workflows/commit-stage.yml)

Spring boot REST API for Polar Bookshop. It keeps the books in the catalog and lets you list them, look one up by isbn, and add, edit or delete them.
the books are saved in postgres now so they stay after the app stops. the table is made by a flyway migration when the app starts.
the root endpoint / gives back a welcome message that comes from the polar.greeting property, so it can be changed from outside without rebuilding.

## Team
- Antonio Lee

## Requirements
- Java 21
- Docker, for the postgres container and for the tests
- Spring Boot 3.5.16. start.spring.io only has 4.x now, so i generated it there and set the version to the newest 3.5.x in build.gradle

## Build and test
```
./gradlew build
```
this also runs all the tests. docker has to be running because the repository and integration tests start their own postgres container with testcontainers, so you dont need the polar-postgres one for the tests. the report is in build/reports/tests/test/index.html

## Run
start postgres first
```
docker run -d --name polar-postgres -e POSTGRES_USER=user -e POSTGRES_PASSWORD=password -e POSTGRES_DB=polardb_catalog -p 5432:5432 postgres:17
./gradlew bootRun
```
Then http://localhost:9001/books

bootRun turns on the testdata profile so it starts with two sample books. java -jar on the built jar doesnt, so only the books you add are there.
if something else is already using port 5432, like a postgres installed on the computer, map the container to another port and point the app at it with an environment variable instead of changing application.yml
```
docker run -d --name polar-postgres -e POSTGRES_USER=user -e POSTGRES_PASSWORD=password -e POSTGRES_DB=polardb_catalog -p 5433:5432 postgres:17
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/polardb_catalog ./gradlew bootRun
```
the user and password in application.yml are only for the local container. a real deployment gives them with SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME and SPRING_DATASOURCE_PASSWORD.
docs/config-demo.md shows the same jar run with a command line argument, a jvm property and an environment variable.

## How to use it
- GET /books lists every book, and GET /books?author=... only lists the books by that author
- POST /books adds a book. if the isbn is already there you get 422, if something in the book is wrong you get 400 with what's wrong
- GET /books/{isbn} gets one book, or 404 if there isnt one with that isbn
- PUT /books/{isbn} changes the book, and if it doesnt exist yet it makes it
- DELETE /books/{isbn} deletes the book and gives back 204

a book needs an isbn with 10 or 13 digits, a title, an author and a price more than 0.
docs/api-demo.md has every one of these tried with curl.

## Project structure
- com.polarbookshop.catalogservice.domain - Book, BookService, BookRepository (spring data jdbc) and the exceptions
- com.polarbookshop.catalogservice.config - PolarProperties for polar.* and DataConfig for auditing
- com.polarbookshop.catalogservice.demo - BookDataLoader, the sample books for the testdata profile
- com.polarbookshop.catalogservice.web - BookController, HomeController and BookControllerAdvice
- src/main/resources/db/migration - the flyway migrations
- src/test/java - validation unit tests, the repository tests and full integration tests against postgres with testcontainers, and the web and json slice tests

## Commit stage
.github/workflows/commit-stage.yml runs on every push. it builds the app, runs the tests, scans the dependencies with grype and puts the report in the Security tab.

## Practicum 1 exercises
E1. PUT gives back 201 now when the book wasnt there and 200 when it was. the controller asks the service if the isbn is already in the catalog, saves it, then picks the status. BookControllerMvcTests has a test for each case and docs/api-demo.md shows both with curl.

E2. before, PUT /books/1111111111 with "isbn": "2222222222" in the body would save the book as 2222222222 but answer 201 for 1111111111, so the url pointed at a book that doesnt exist. thats not ok, the isbn in the url is the book you are talking about. now if the two dont match it gives 400 with a message saying they have to match, and nothing gets saved. BookControllerMvcTests has a test for it.

E3. @ValidIsbn checks the real check digit. for ISBN-10 the digits get multiplied by 10 down to 1 and the sum has to divide by 11, for ISBN-13 they get multiplied by 1, 3, 1, 3 and the sum has to divide by 10. if the isbn is empty or the wrong length it lets @NotBlank and @Pattern handle that so you dont get two errors. IsbnValidatorTests tries real isbns and ones with a wrong last digit. i didnt put it on Book because the isbns in the handout, like 1234567890, arent real isbns and every demo and test would fail, but it could go on Book once real isbns are used.

E4. DELETE on a book that isnt there still gives 204 and i think thats right. DELETE is supposed to be idempotent, sending it once or ten times leaves the server the same way, with no book with that isbn. the client wanted it gone and it is gone, so a 404 would make the second delete look like it failed when nothing went wrong.

E5. CatalogServiceApplicationTests has a test that does POST, GET, PUT, DELETE and then GET again on the same book against the real server, and the last GET has to be 404.

## Practicum 2 exercises
E1. V2__Add_publisher_column.sql adds a publisher column and Book has a publisher field now, Book.of takes it too. the column has to be nullable in this release. all the books already in the table dont have a publisher, so a not null column couldnt even be added. and while a new version rolls out the old version is still running next to it, the old code doesnt know about publisher and its inserts would fail if the column was required.
to make it mandatory later i would do it in two releases. release 1 is this one, the column is nullable, the new code saves a publisher and i fill in the publisher for the old rows. release 2 only goes out once every instance runs release 1 and every row has a publisher, then a V3 migration sets it to not null and Book gets @NotBlank on publisher. that way no running version ever writes a row the database rejects.

E2. GET /books?author=Lyra Silverstar gives back only the books by that author. BookRepository has findAllByAuthor, a derived query so spring data writes the sql from the method name, and the controller uses it when the author param is there and lists everything when its not. BookRepositoryJdbcTests checks it against postgres and BookControllerMvcTests checks the endpoint with and without the param.
