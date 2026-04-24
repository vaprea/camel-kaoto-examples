# Send and Receive Messages with Camel CLI and camel infra

This guide explains how to send and receive messages directly from the terminal using the Camel JBang CLI commands `camel cmd send` and `camel cmd receive`, paired with `camel infra` to spin up the required infrastructure locally — without writing a full route file.

These commands are useful for quick testing, debugging, and ad-hoc message injection.

## Supported Infrastructure Systems

Run the following to see all available systems:

```bash
camel infra list
```

The table below lists the systems currently supported (as of Camel 4.18):

| Alias | Implementations | Description |
|-------|----------------|-------------|
| `arangodb` | | ArangoDB multi-model database |
| `artemis` | `amqp` | Apache Artemis message broker |
| `aws` | `sqs`, `sns`, `s3`, `kinesis`, `dynamo-db`, `lambda`, and more | Local AWS services via LocalStack |
| `azure` | `storage-blob`, `storage-queue` | Local Azure services via Azurite |
| `cassandra` | | Apache Cassandra NoSQL database |
| `elasticsearch` | | Elasticsearch NoSQL database |
| `ftp` | | Embedded FTP server |
| `ftps` | | Embedded FTPS server |
| `google` | `pub-sub` | Google Cloud services |
| `hazelcast` | | Hazelcast in-memory database |
| `ibmmq` | | IBM MQ messaging middleware |
| `iggy` | | Iggy distributed message streaming |
| `kafka` | `confluent`, `redpanda`, `strimzi` | Apache Kafka event streaming platform |
| `minio` | | MinIO object storage (S3-compatible) |
| `mongodb` | | MongoDB NoSQL database |
| `mosquitto` | | Mosquitto MQTT message broker |
| `nats` | | NATS messaging platform |
| `postgres` | | PostgreSQL database |
| `pulsar` | | Apache Pulsar messaging and streaming |
| `rabbitmq` | | RabbitMQ messaging broker |
| `redis` | | Redis in-memory database |
| `sftp` | | Embedded SFTP server |

To start a system:

```bash
camel infra run <alias>
```

To start a specific implementation:

```bash
camel infra run <alias> --impl=<implementation>
```

---

## Send a Message

Use `camel cmd send` to publish a single message to an endpoint:

```bash
camel cmd send --endpoint="<endpoint-uri>" --infra=<alias> --body "<message>"
```

### Options

| Option | Description |
|--------|-------------|
| `--endpoint=<uri>` | Camel endpoint URI to send the message to |
| `--infra=<alias>` | Infrastructure alias matching the running `camel infra run` service |
| `--body <text>` | Message body (string) |
| `--header <key=value>` | Add a message header (repeatable) |

### Example — Kafka

Start the broker:

```bash
camel infra run kafka
```

Send a plain text message:

```bash
camel cmd send --endpoint="kafka:logs-topic" --infra=kafka --body "Sample message"
```

Send a JSON payload:

```bash
camel cmd send --endpoint="kafka:logs-topic" --infra=kafka \
  --body '{"event":"order-placed","orderId":42}' \
  --header "Content-Type=application/json"
```

---

## Receive Messages

Use `camel cmd receive` to consume messages from an endpoint and print them to the console:

```bash
camel cmd receive --endpoint="<endpoint-uri>" --infra=<alias>
```

The command keeps running and prints each incoming message until you press `Ctrl+C`.

### Options

| Option | Description |
|--------|-------------|
| `--endpoint=<uri>` | Camel endpoint URI to receive messages from |
| `--infra=<alias>` | Infrastructure alias matching the running `camel infra run` service |
| `--since=<duration>` | Show only messages newer than a relative duration (e.g. `30s`, `5m`, `1h`). Bare numbers are interpreted as seconds. |
| `--pretty` | Pretty-print the message body when it is JSON or XML. |
| `--action=<action>` | Control what the command does: `status` (default) shows current messages, `start` begins receiving, `stop` halts receiving, `clear` discards buffered messages, `dump` outputs all collected messages. |

### Example — Kafka

Receive all messages from a topic:

```bash
camel cmd receive --endpoint="kafka:logs-topic" --infra=kafka
```

Receive messages arrived in the last 2 minutes, pretty-printed:

```bash
camel cmd receive --endpoint="kafka:logs-topic" --infra=kafka --since=2m --pretty
```

Dump all buffered messages to the console:

```bash
camel cmd receive --endpoint="kafka:logs-topic" --infra=kafka --action=dump
```

---

## End-to-End Example (Kafka)

Open **two terminals**.

**Terminal 1 — start the broker and consumer:**

```bash
camel infra run kafka
```

```bash
camel cmd receive --endpoint="kafka:logs-topic" --infra=kafka
```

**Terminal 2 — send a test message:**

```bash
camel cmd send --endpoint="kafka:logs-topic" --infra=kafka --body "Hello from Camel CLI"
```

You should see the message appear immediately in Terminal 1.

---

## Stop the Infrastructure

```bash
camel infra stop <alias>
```

Or press `Ctrl+C` in the terminal running `camel infra run <alias>`.

---

## Related

- [kafka/send-receive/README.md](../kafka/send-receive/README.md) — full route-based Kafka example using `camel run`
- [Camel JBang reference](https://camel.apache.org/manual/camel-jbang.html)
