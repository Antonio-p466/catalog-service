Config demo

i built the jar one time with ./gradlew bootJar and then ran that same jar four different ways without rebuilding. after each start i called the root endpoint with curl and then stopped it with ctrl-c. the jar didnt change between runs, only how polar.greeting got passed in.

with nothing passed in it uses the default from application.yml.

    $ java -jar build/libs/catalog-service-0.0.1-SNAPSHOT.jar
    $ curl localhost:9001/
    Welcome to the local book catalog!

1. command line argument

    $ java -jar build/libs/catalog-service-0.0.1-SNAPSHOT.jar --polar.greeting="Welcome to the catalog from CLI"
    $ curl localhost:9001/
    Welcome to the catalog from CLI

2. jvm system property

    $ java -Dpolar.greeting="Welcome to the catalog from JVM" -jar build/libs/catalog-service-0.0.1-SNAPSHOT.jar
    $ curl localhost:9001/
    Welcome to the catalog from JVM

3. environment variable. polar.greeting turns into POLAR_GREETING because of relaxed binding

    $ POLAR_GREETING="Welcome to the catalog from ENV" java -jar build/libs/catalog-service-0.0.1-SNAPSHOT.jar
    $ curl localhost:9001/
    Welcome to the catalog from ENV

4. jvm property and command line argument at the same time

    $ java -Dpolar.greeting="Welcome to the catalog from JVM" -jar build/libs/catalog-service-0.0.1-SNAPSHOT.jar --polar.greeting="Welcome to the catalog from CLI"
    $ curl localhost:9001/
    Welcome to the catalog from CLI

the command line argument wins. its at the top of spring's list, then jvm properties, then environment variables, then the profile files, then application.yml in the jar. so application.yml is only the default and anything passed in from outside beats it.
