# Kafka Order Router

## Overview

This example shows how **Apache Camel** with **Kaoto** can consume e-commerce order events from an Apache Kafka topic, classify them by urgency using a **Content-Based Router**, and dispatch them to separate JMS queues.

Two Camel routes are defined:

- **`order-producer.camel.yaml`** — two timer-driven producer routes that publish order events to the `orders-topic` Kafka topic: one sends an urgent order every 5 seconds, the other a standard order every 3 seconds.
- **`order-router.camel.yaml`** — a consumer route that reads from `orders-topic`, inspects the `type` field of each JSON event using a JSONPath predicate, and routes urgent orders to `jms:queue:priority-orders` or standard orders to `jms:queue:standard-orders`.

### EIP Patterns

| Pattern | Where |
|---------|-------|
| **Content-Based Router** | `choice` node in `order-router.camel.yaml` — branches on `$.type == 'urgent'` |
| **Message Channel** | Two dedicated JMS queues act as typed channels for downstream consumers |

### What it looks like in Kaoto

Opening `order-router.camel.yaml` in the Kaoto editor renders the `choice` node as a branching visual graph — the `when` branch points to the priority queue and the `otherwise` branch points to the standard queue. This makes the routing logic immediately legible to a non-technical audience.

## Prerequisites

See [Prerequisites](../../README.md#prerequisites) in the root README. Docker and Docker Compose are required to start the Kafka broker and ActiveMQ Artemis broker.

## Running the Example

### 1. Start the infrastructure

```bash
docker compose -f docker-compose.yml up -d
```

This starts:
- An Apache Kafka broker on `localhost:9092`
- An ActiveMQ Artemis broker on `localhost:61616` (web console at `http://localhost:8161`, credentials `admin` / `admin`)

### 2. Run the Camel routes

Open two terminals inside the `order-router/` directory.

**Terminal 1 — Router (start first):**

```bash
camel run order-router.camel.yaml \
  --deps=org.apache.activemq:artemis-jms-client:2.40.0
```

**Terminal 2 — Producer:**

```bash
camel run order-producer.camel.yaml
```

Start the router first so it is subscribed to the topic before the producer begins publishing.

Expected output in the router terminal:

```
[router] Order received: {"orderId":"ORD-S1","type":"standard","customer":"Bob","amount":45}
[router] Routing to standard queue: {"orderId":"ORD-S1",...}
[router] Order received: {"orderId":"ORD-U1","type":"urgent","customer":"Alice","amount":750}
[router] Routing to priority queue: {"orderId":"ORD-U1",...}
```

You can also open the ActiveMQ web console at `http://localhost:8161` to browse the `priority-orders` and `standard-orders` queues and verify that messages are arriving on the correct channel.

### 3. Open in Kaoto

Open `order-router.camel.yaml` in the [Kaoto VS Code extension](https://marketplace.visualstudio.com/items?itemName=redhat.vscode-kaoto) to visualise the branching `choice` node graphically.

### 4. Stop the infrastructure

```bash
docker compose -f docker-compose.yml down
```

## Project Structure

```text
order-router/
├── docker-compose.yml          # Kafka broker + ActiveMQ Artemis broker
├── application.properties      # Wires the JMS ConnectionFactory bean
├── order-producer.camel.yaml   # Producer routes (timer → Kafka)
└── order-router.camel.yaml     # Router route (Kafka → choice → JMS queues)
```
