# API demo

the app running with ./gradlew bootRun on port 9001, every status code from table 1 except 201 on PUT (thats exercise E1). used curl, the Date and Keep-Alive headers are cut out.

## 200 list books (empty at first)
```
$ curl -i localhost:9001/books
HTTP/1.1 200 
Content-Type: application/json

[]
```

## 201 add a book
```
$ curl -i -X POST localhost:9001/books -H 'Content-Type: application/json' -d '{"isbn":"1234567891","title":"Northern Lights","author":"Lyra Silverstar","price":9.90}'
HTTP/1.1 201 
Content-Type: application/json

{"isbn":"1234567891","title":"Northern Lights","author":"Lyra Silverstar","price":9.9}
```

## 200 get the book by isbn
```
$ curl -i localhost:9001/books/1234567891
HTTP/1.1 200 
Content-Type: application/json

{"isbn":"1234567891","title":"Northern Lights","author":"Lyra Silverstar","price":9.9}
```

## 200 list books
```
$ curl -i localhost:9001/books
HTTP/1.1 200 
Content-Type: application/json

[{"isbn":"1234567891","title":"Northern Lights","author":"Lyra Silverstar","price":9.9}]
```

## 422 add the same isbn again
```
$ curl -i -X POST localhost:9001/books -H 'Content-Type: application/json' -d '{"isbn":"1234567891","title":"Northern Lights","author":"Lyra Silverstar","price":9.90}'
HTTP/1.1 422 
Content-Type: text/plain;charset=UTF-8
Content-Length: 43

A book with ISBN 1234567891 already exists.
```

## 400 invalid book (bad isbn and empty title)
```
$ curl -i -X POST localhost:9001/books -H 'Content-Type: application/json' -d '{"isbn":"123ABC456Z","title":"","author":"Jon Snow","price":9.90}'
HTTP/1.1 400 
Content-Type: application/json

{"isbn":"The ISBN format must be valid.","title":"The book title must be defined."}
```

## 404 isbn that doesnt exist
```
$ curl -i localhost:9001/books/9999999999
HTTP/1.1 404 
Content-Type: text/plain;charset=UTF-8
Content-Length: 44

The book with ISBN 9999999999 was not found.
```

## 200 update the book with PUT
```
$ curl -i -X PUT localhost:9001/books/1234567891 -H 'Content-Type: application/json' -d '{"isbn":"1234567891","title":"Northern Lights","author":"Lyra Silverstar","price":12.50}'
HTTP/1.1 200 
Content-Type: application/json

{"isbn":"1234567891","title":"Northern Lights","author":"Lyra Silverstar","price":12.5}
```

## 204 delete the book
```
$ curl -i -X DELETE localhost:9001/books/1234567891
HTTP/1.1 204
```

## 404 the deleted book is gone
```
$ curl -i localhost:9001/books/1234567891
HTTP/1.1 404 
Content-Type: text/plain;charset=UTF-8
Content-Length: 44

The book with ISBN 1234567891 was not found.
```
