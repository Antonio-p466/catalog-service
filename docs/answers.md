Bonus questions

1. Why is a self-contained JAR with an embedded server better for the cloud than a WAR on a shared app server?

the jar has the server inside it so all it needs is a JVM. the same jar runs the same way on my laptop, in CI and in production, and you can start more copies of it whenever you want. a WAR needs an app server that someone installed and set up first, and other apps share that server, so one app can mess with another and you cant just scale one of them by itself.

2. What happens to the 51st request at the same time with the step 2.2 config? Two ways to handle more load, and which one does a cloud native app use?

max threads is 50 so all 50 are busy and the 51st has to wait in the queue until one of them finishes. if the queue fills up too, new connections get refused. you can scale vertically, a bigger machine with more threads, or horizontally, more instances of the app behind a load balancer. a cloud native app goes horizontal because adding and removing instances is quick and one instance going down doesnt take everything down.

3. Why does InMemoryBookRepository use a ConcurrentHashMap instead of a HashMap?

tomcat handles requests on a lot of threads at the same time and they all use the same map. a HashMap isnt thread safe, two threads writing at once can lose a book or break the map, and reading while another thread writes can throw an exception. ConcurrentHashMap is made for that so it stays correct.

4. Why is 422 better than 400 for a duplicate ISBN?

400 means the request itself is wrong, like bad json or a field that fails validation. a duplicate isbn is a fine request, the book is valid, the server just cant do it because that isbn is already taken. 422 says exactly that, and the client can tell "fix your book" (400) apart from "this one already exists" (422).

5. One advantage and one limitation of @WebMvcTest compared with @SpringBootTest(webEnvironment = RANDOM_PORT)?

advantage: its a lot faster because it only loads the controller and the advice and mocks the service, so you test the web layer by itself. limitation: the service is a mock, so it doesnt test the real service and repository together or a real http call to a running server, a bug between the layers wouldnt show up.

6. Why should the commit stage be fast, and what if a slow test makes it go over its time?

it runs on every push and the whole point is quick feedback, if it takes too long people stop waiting for it and push more on top of a broken build. if a slow test makes it go over, make the test faster if you can, or move it to the acceptance stage where slower tests belong, instead of letting the commit stage get slow.
