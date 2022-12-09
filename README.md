# Simple blog based on MongoDB

[![Java CI](https://github.com/mfvanek/blog-using-mongodb/actions/workflows/tests.yml/badge.svg)](https://github.com/mfvanek/blog-using-mongodb/actions/workflows/tests.yml)
[![codecov](https://codecov.io/gh/mfvanek/blog-using-mongodb/branch/master/graph/badge.svg?token=R66V4MPEU1)](https://codecov.io/gh/mfvanek/blog-using-mongodb)

## Technology stack
- Java 11
- [Maven](https://maven.apache.org/)
- [Spark Framework](http://sparkjava.com) (with embedded Jetty)
- [Freemarker](https://freemarker.apache.org)
- [Logback](https://logback.qos.ch)
- [Lombok](https://projectlombok.org)
- [JUnit 5](https://junit.org/junit5/)

## How to run
Run MongoDB in Docker
```bash
docker run -d --name mongo-blog -p 27017:27017 mongo:4.4.17
```

Go to page [http://localhost:8082/login](http://localhost:8082/login)
