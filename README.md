# Camel Kaoto Examples

A collection of self-contained examples showing how to design, visualise, and run **Apache Camel** integration routes using **[Kaoto](https://kaoto.io/)** — the visual editor for Camel.

Each example targets a specific integration use case and is fully runnable with [Camel JBang](https://camel.apache.org/manual/camel-jbang.html). Routes are authored as `.camel.yaml` files that can be opened directly in the [Kaoto VS Code extension](https://marketplace.visualstudio.com/items?itemName=redhat.vscode-kaoto) for graphical editing.

## Examples

| Folder | Description |
|--------|-------------|
| [kafka/send-receive](kafka/send-receive/README.md) | Publish and consume messages on an Apache Kafka topic using Camel's `kafka` component. Includes a Docker Compose file to spin up a local broker. |
| [ftp](ftp/README.md) | Upload files to an FTP server and read them back with safe idempotent polling (`noop=true`, `idempotent=true`). Uses `camel infra run ftp` for a zero-config embedded FTP server. |

## Prerequisites

- [Visual Studio Code](https://code.visualstudio.com/)
  - [Extension Pack for Apache Camel by Red Hat](https://marketplace.visualstudio.com/items?itemName=redhat.apache-camel-extension-pack) — includes the Kaoto visual editor
- [JBang](https://www.jbang.dev/)
  - [Camel JBang](https://camel.apache.org/manual/camel-jbang.html) (Apache Camel CLI)
- [Docker](https://www.docker.com/) and Docker Compose — only needed for examples that offer a Docker Compose option

## Getting Started

1. Clone this repository.
2. Navigate to the example folder you want to run.
3. Follow the instructions in the example's own `README.md`.

## Contributing

To add a new example, create a folder under the relevant technology category (e.g. `kafka/`, `http/`, `database/`) and include:

- One or more `.camel.yaml` route files
- A `docker-compose.yml` if external infrastructure is needed
- A `README.md` describing the use case, prerequisites, and run instructions

Then add a row to the table above.
