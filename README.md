# Simple blog based on MongoDB

## Technology stack
- Java 8
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
