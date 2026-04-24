# Camel infra

Camel Test Infra is a utility framework that simplifies testing Camel applications by provisioning the external infrastructure they depend on — message brokers, databases, file servers, cloud service emulators, and more.

It abstracts the infrastructure setup away from test logic through service interfaces and manages container lifecycles via [Testcontainers](https://testcontainers.com/), so tests can run against real (containerised) services without manual setup.

## How It Works

Each supported system is exposed through a typed service interface (e.g. `JMSService`, `JDBCService`) that decouples test code from the underlying implementation. At runtime the framework:

1. Starts the required container image (or connects to a remote instance)
2. Registers connection details as properties (host, port, URI, credentials)
3. Makes those properties available to Camel routes and Spring XML configurations

Supported deployment modes:

| Mode | Description |
|------|-------------|
| Local container | Docker image managed by Testcontainers (default) |
| Remote instance | Connect to an already-running external service |
| Embedded component | In-process service (where supported, e.g. FTP) |

## Using Test Infra from the CLI

`camel infra` exposes the same services directly from the terminal, without writing any test code. This is useful for local development, prototyping, and manual testing.

List available services:

```bash
camel infra list
```

Start a service:

```bash
camel infra run <alias>
```

Start a specific implementation of a service:

```bash
camel infra run <alias> --impl=<implementation>
```

Check running services:

```bash
camel infra ps
```

Stop a service:

```bash
camel infra stop <alias>
```

## Using Test Infra in JUnit Tests

Add the dependency for the service you need:

```xml
<dependency>
    <groupId>org.apache.camel</groupId>
    <artifactId>camel-test-infra-kafka</artifactId>
    <version>${camel.version}</version>
    <scope>test</scope>
</dependency>
```

Declare the service as a JUnit 5 extension:

```java
@RegisterExtension
static KafkaService kafka = KafkaServiceFactory.createService();
```

The extension starts the container before tests run and stops it afterwards. Connection properties (e.g. `kafka.bootstrap.servers`) are automatically registered and available to Camel routes via property placeholders.

## Relationship with Citrus

Test Infra handles **infrastructure provisioning**. It does not define test scenarios or validate message content — that is the responsibility of the test framework (JUnit, Citrus, etc.). When used together with Citrus, Test Infra services can be started and stopped as part of the Citrus test lifecycle, with connection details exposed as Citrus test variables.

See [infra-camel-citrus.md](infra-camel-citrus.md) for the Citrus side of the story.

## References

- [Test Infrastructure — Apache Camel](https://camel.apache.org/manual/test-infra.html)
- [Testing Camel — Apache Camel](https://camel.apache.org/manual/testing.html)
