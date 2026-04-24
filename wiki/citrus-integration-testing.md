# Citrus Integration Testing

[Citrus](https://citrusframework.org/) is an open-source Java testing framework focused on integration testing and messaging. In the context of Apache Camel it enables writing automated tests that verify how Camel routes interact with external services — sending and receiving real messages and asserting their content.

## What Citrus Provides

- **Message validation** for JSON, XML, YAML, and plain text payloads
- **Client/server simulation** — act as a sender, a receiver, or both within the same test
- **Integration with JUnit Jupiter** as a test engine
- **Spring Boot and Quarkus** support for testing Camel applications in their runtime
- **JBang integration** — write and run Citrus tests from the CLI during prototyping, without a full Maven project

## Using Citrus with Camel JBang

Citrus is the engine behind the `camel test` JBang plugin. Install the plugin first:

```bash
camel plugin add test
```

Initialise a test file (supports YAML, Groovy, Java, XML, Cucumber):

```bash
camel test init route-test.yaml
```

Run the test:

```bash
camel test run route-test.yaml
```

Test files are placed in a `test/` subfolder to keep them separate from runtime route sources. Additional dependencies are declared in `jbang.properties` under the `run.deps` property using Maven GAV coordinates.

Citrus can also be installed standalone as a JBang app:

```bash
jbang app install citrus@citrusframework/citrus
```

## Using Camel Test Infra Services in Citrus Tests

When a `camel infra` service is running, Citrus automatically exposes its connection settings as test variables following this naming convention:

```
CITRUS_CAMEL_INFRA_SERVICE_<SERVICE_NAME>_<PROPERTY_NAME>
```

For example, starting a PostgreSQL service with `camel infra run postgres` would expose variables such as:

```
CITRUS_CAMEL_INFRA_SERVICE_POSTGRES_HOST
CITRUS_CAMEL_INFRA_SERVICE_POSTGRES_PORT
CITRUS_CAMEL_INFRA_SERVICE_POSTGRES_DB_NAME
```

These variables can be referenced directly in `application.properties` to configure Camel route endpoints:

```properties
camel.datasource.url=jdbc:postgresql://${CITRUS_CAMEL_INFRA_SERVICE_POSTGRES_HOST}:${CITRUS_CAMEL_INFRA_SERVICE_POSTGRES_PORT}/mydb
```

This means the test infrastructure and the route configuration share the same connection details automatically, with no hardcoded values in the test.

Citrus can also manage the infra service lifecycle programmatically within the test:

```java
t.$(camelInfra().service("kafka").start());
// ... test steps ...
t.$(camelInfra().service("kafka").stop());
```

## Using Citrus in a Maven Project

Add the Citrus Camel dependency:

```xml
<dependency>
    <groupId>org.citrusframework</groupId>
    <artifactId>citrus-camel</artifactId>
    <version>${citrus.version}</version>
    <scope>test</scope>
</dependency>
```

A minimal JUnit 5 test that sends a message to a Camel route and verifies the response:

```java
@ExtendWith(CitrusExtension.class)
class MyRouteIT {

    @Test
    void shouldProcessMessage(@CitrusResource TestCaseRunner t) {
        t.$(send("direct:input")
            .message()
            .body("Hello Camel"));

        t.$(receive("direct:output")
            .message()
            .body("Hello Camel processed"));
    }
}
```

Citrus endpoints can also be declared as Spring beans and injected into tests, which is the preferred approach when using Spring Boot:

```java
@Bean
public DirectEndpoint inputEndpoint() {
    return CitrusEndpoints.direct().asynchronous().queue("input").build();
}
```


## Kaoto Tests Panel

Kaoto includes a dedicated **Tests** panel that provides a graphical interface for managing and running Citrus integration tests without leaving the IDE.

### Creating a Test

Open the Tests panel from the Kaoto sidebar. Click **New Test** to scaffold a new test file — Kaoto generates the appropriate Citrus test structure (YAML, Groovy, Java, or XML) and places it in the `test/` subfolder alongside your routes.

### Running Tests

Select one or more tests in the panel and click **Run**. Kaoto invokes `camel test run` under the hood, streams the output into the panel, and marks each test as passed or failed. No terminal interaction is required.

### Integration with Camel Infra Services

When a `camel infra` service is active, the Tests panel reflects its status. Tests that depend on infra services (Kafka, PostgreSQL, etc.) can reference the automatically exposed `CITRUS_CAMEL_INFRA_SERVICE_*` variables — Kaoto wires these up without any manual configuration.

## References

- [Integration Test — Apache Camel](https://camel.apache.org/manual/integration-test.html)
- [Camel Testing plugin — Apache Camel](https://camel.apache.org/manual/camel-jbang-test.html)
- [Test Infrastructure — Apache Camel](https://camel.apache.org/manual/test-infra.html)
- [Citrus Framework](https://citrusframework.org/)
