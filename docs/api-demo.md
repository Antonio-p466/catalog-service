API demo

i ran the app with ./gradlew bootRun on port 9001 and tried every status code from table 1 with curl. the only one not here is 201 on PUT, thats exercise E1 and it gets fixed later. i cut out the Date and Keep-Alive lines from the responses.

first i listed the books. nothing there yet so its an empty list and 200.

    $ curl -i localhost:9001/books
    HTTP/1.1 200 
    Content-Type: application/json

    []

added a book. 201 and it sends the book back.

    $ curl -i -X POST localhost:9001/books -H 'Content-Type: application/json' -d '{"isbn":"1234567891","title":"Northern Lights","author":"Lyra Silverstar","price":9.90}'
    HTTP/1.1 201 
    Content-Type: application/json

    {"isbn":"1234567891","title":"Northern Lights","author":"Lyra Silverstar","price":9.9}

got that book by its isbn. 200.

    $ curl -i localhost:9001/books/1234567891
    HTTP/1.1 200 
    Content-Type: application/json

    {"isbn":"1234567891","title":"Northern Lights","author":"Lyra Silverstar","price":9.9}

listed again and now the book is in there.

    $ curl -i localhost:9001/books
    HTTP/1.1 200 
    Content-Type: application/json

    [{"isbn":"1234567891","title":"Northern Lights","author":"Lyra Silverstar","price":9.9}]

tried adding the same isbn again. 422 because it already exists.

    $ curl -i -X POST localhost:9001/books -H 'Content-Type: application/json' -d '{"isbn":"1234567891","title":"Northern Lights","author":"Lyra Silverstar","price":9.90}'
    HTTP/1.1 422 
    Content-Type: text/plain;charset=UTF-8
    Content-Length: 43

    A book with ISBN 1234567891 already exists.

sent a bad book, the isbn has letters and the title is empty. 400 and it says which fields are wrong.

    $ curl -i -X POST localhost:9001/books -H 'Content-Type: application/json' -d '{"isbn":"123ABC456Z","title":"","author":"Jon Snow","price":9.90}'
    HTTP/1.1 400 
    Content-Type: application/json

    {"isbn":"The ISBN format must be valid.","title":"The book title must be defined."}

asked for an isbn that doesnt exist. 404.

    $ curl -i localhost:9001/books/9999999999
    HTTP/1.1 404 
    Content-Type: text/plain;charset=UTF-8
    Content-Length: 44

    The book with ISBN 9999999999 was not found.

changed the price with PUT. 200 and the new price comes back.

    $ curl -i -X PUT localhost:9001/books/1234567891 -H 'Content-Type: application/json' -d '{"isbn":"1234567891","title":"Northern Lights","author":"Lyra Silverstar","price":12.50}'
    HTTP/1.1 200 
    Content-Type: application/json

    {"isbn":"1234567891","title":"Northern Lights","author":"Lyra Silverstar","price":12.5}

deleted the book. 204 and nothing comes back.

    $ curl -i -X DELETE localhost:9001/books/1234567891
    HTTP/1.1 204

asked for it again after deleting and its gone, 404.

    $ curl -i localhost:9001/books/1234567891
    HTTP/1.1 404 
    Content-Type: text/plain;charset=UTF-8
    Content-Length: 44

    The book with ISBN 1234567891 was not found.
