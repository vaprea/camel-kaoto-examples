# Kafka Send and Receive

## Overview

This example demonstrates how to use **Apache Camel** with **Kaoto** to send and receive messages through **Apache Kafka**.

Two Camel routes are defined:

- **`kafka-send.camel.yaml`** — a producer route that fires every 2 seconds (via a `timer` component), sets the body to a fixed string (`Sample message`), publishes it to the `logs-topic` Kafka topic, then logs the message body to the console.
- **`kafka-receive.camel.yaml`** — a consumer route that subscribes to the `logs-topic` Kafka topic (consumer group `myGroup`, offset reset `earliest`) and logs every received message to the console.

Both routes connect to a Kafka broker running at `localhost:9092`.

## Prerequisites

- [Docker](https://www.docker.com/) and Docker Compose
- [Camel JBang](https://camel.apache.org/manual/camel-jbang.html) (Apache Camel CLI) with the Kaoto plugin

## Running the Example

### 1. Start the Kafka broker

A single-node Kafka broker (KRaft mode, no Zookeeper) is provided via Docker Compose:

```bash
docker compose -f docker-compose.yml up -d
```

This starts an `apache/kafka:4.1.1` container listening on `localhost:9092`.

### 2. Run the Camel routes

Open two separate terminals inside the `send-receive/` directory and start each route independently with Camel JBang.

**Terminal 1 — Consumer (receive first):**

```bash
jbang camel@apache/camel run kafka-receive.camel.yaml
```

**Terminal 2 — Producer (send):**

```bash
jbang camel@apache/camel run kafka-send.camel.yaml
```

Start the consumer first so it is ready to receive messages before the producer begins sending.

You should see the producer printing `Sample message` every 2 seconds, and the consumer printing `Messaggio ricevuto da Kafka Sample message` as messages arrive.

### 3. Open in Kaoto

You can open either `.camel.yaml` file directly in the [Kaoto VS Code extension](https://marketplace.visualstudio.com/items?itemName=redhat.vscode-kaoto) to visualise and edit the routes graphically.

### 4. Stop the Kafka broker

```bash
docker compose -f docker-compose.yml down
```

## Project Structure

```text
send-receive/
├── docker-compose.yml          # Single-node Kafka broker (KRaft)
├── kafka-send.camel.yaml       # Producer route (timer → Kafka)
└── kafka-receive.camel.yaml    # Consumer route (Kafka → log)
```
