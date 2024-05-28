# grpc examples using maven and java 21

Uses the following:
* `Java 21` early access
* maven wrapper with `maven 3.6.3`

## Build & Run locally

* Build project using maven wrapper first
```bash
./mvnw clean package -DskipTests
```

## Unit tests.

For unit tests execute the following command
```bash
./mvnw clean test
```

## Code Style

Code formatted using [spotless-maven-plugin](https://github.com/diffplug/spotless/tree/master/plugin-maven ). The `spotless:apply` called just before the compilation 
phase to format code properly.

## References


## Backlog

* Add `sl4j/logback` as logger instead of `System.out/System.err`
* Add unique node ID to logs
* Add proper unit and integration testing
* Add jacoco code coverage
* Add `Architecture Decision Record` folder with most important decisions
* Add performance testing using JMH or k6
* Create scripts to start cluster with many nodes and appropriate logging




