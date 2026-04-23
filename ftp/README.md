# FTP Read and Log

## Overview

This example demonstrates how to use **Apache Camel** with **Kaoto** to send and receive files through an **FTP server**.

Two Camel routes are defined:

- **`ftp-send.camel.yaml`** — a producer route that fires every 5 seconds (via a `timer` component), sets the body to a fixed string, uploads it as a uniquely named `.txt` file to the FTP server, then logs the message body to the console.
- **`ftp-read.camel.yaml`** — a consumer route that polls the FTP server for new files, reads each file once (idempotent consumer), leaves the file in place (`noop=true`), and logs the file name and content to the console.

Both routes connect to an FTP server running at `localhost:2121` with credentials `admin` / `admin`.

The consumer is intentionally safe:
- **`noop=true`** — files are never moved or deleted after being read.
- **`idempotent=true`** — each file is processed exactly once, even if the route restarts.

## Prerequisites

See [Prerequisites](../README.md#prerequisites) in the root README. Docker is not required for this example.

## Running the Example

### 1. Start the FTP server

Camel JBang can spin up an embedded FTP server with a single command — no Docker or separate installation required:

```bash
camel infra run ftp
```

This starts an embedded FTP server on `localhost:2121` with credentials `admin` / `admin`.

Press `Ctrl+C` to stop it when done.

### 2. Run the Camel routes

Open two separate terminals inside the `ftp/` directory and start each route independently with Camel JBang.

**Terminal 1 — Consumer (start first):**

```bash
camel run ftp-read.camel.yaml
```

**Terminal 2 — Producer (send files):**

```bash
camel run ftp-send.camel.yaml
```

Start the consumer first so it is ready before the producer begins uploading files.

You should see the producer printing `Sent file to FTP with content Sample message from FTP sender` every 5 seconds, and the consumer printing `Received file message-<timestamp>.txt with content Sample message from FTP sender` as new files arrive.

Because `idempotent=true` and `noop=true` are set, restarting the consumer will not re-process files that have already been logged.

### 3. Open in Kaoto

You can open either `.camel.yaml` file directly in the [Kaoto VS Code extension](https://marketplace.visualstudio.com/items?itemName=redhat.vscode-kaoto) to visualise and edit the routes graphically.

## Project Structure

```text
ftp/
├── ftp-send.camel.yaml    # Producer route (timer → FTP)
└── ftp-read.camel.yaml    # Consumer route (FTP → log, noop + idempotent)
```
