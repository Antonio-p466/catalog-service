Bonus questions, practicum 2

1. Why is it a mistake to name feature profiles dev and prod?

a profile like prod ties the code to a list of environments. if we add a staging environment later we would have to change the code or copy the prod profile, and you cant tell from the name what it actually turns on. naming it after the feature, like testdata, says what it does and you can turn it on in whatever environment needs it.

2. polar.greeting is set in application.yml, an environment variable and a JVM property. Which value is used, and why is the environment variable still the recommended way?

the jvm property wins because its higher up in spring's order than environment variables, and both beat application.yml. the environment variable is still recommended because every os and container platform supports them, and the command that starts the app stays the same everywhere. you only change the environment around it, not the java command.

3. What makes Catalog Service stateless after Part B, and why can three instances run behind a load balancer?

the books are in postgres now and not in a map inside the app, so the app itself doesnt keep anything between requests. any instance can answer any request because they all read and write the same database. if one goes down or a new one starts nothing is lost, so the load balancer can send a request to any of the three.

4. Why test against PostgreSQL in a container instead of an in-memory H2 database?

so the tests run on the same database as production. H2 has different sql and types, and things can pass on H2 and break on postgres. i actually ran into that in the last project, id identity and table name quoting worked on H2 and broke on postgres. testcontainers gives a real postgres 17 that also runs the real flyway migrations, and it gets thrown away after the tests.

5. Why must you never change V1__Initial_schema.sql once it was applied to a shared database?

flyway saves a checksum of every migration it ran in flyway_schema_history. if the file changes the checksum doesnt match and flyway refuses to start. and even if it didnt, databases that already ran the old V1 would never run the new one, so they would end up with different schemas. a change goes in a new migration like V2 so every database gets it the same way.

6. Why does BookRepositoryJdbcTests insert its test data with JdbcAggregateTemplate instead of bookRepository.save?

the test is testing the repository, so the setup shouldnt use the thing being tested. if save was broken the setup would break too and the test wouldnt show what actually went wrong. JdbcAggregateTemplate is the lower level tool spring data jdbc uses underneath, so it puts the data in without going through the repository methods.
