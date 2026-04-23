# SOAP Request

## Overview

This example demonstrates how to use **Apache Camel** with **Kaoto** to call a **SOAP web service** over HTTP.

A single Camel route is defined:

- **`soap-sample.camel.yaml`** — a one-shot route that fires once at startup (via the `once` component), builds a SOAP envelope that calls the `Add` operation of a public calculator service, sends it over HTTP, extracts the result from the response using XPath, and logs it to the console.

The route targets the public endpoint `http://www.dneonline.com/calculator.asmx` — no local infrastructure is required.

## Prerequisites

See [Prerequisites](../README.md#prerequisites) in the root README. No Docker is required for this example.

## Running the Example

Run the route with Camel JBang from inside the `soap-request/` directory:

```bash
camel run soap-sample.camel.yaml
```

The route fires once and logs two lines:

```
RAW RESPONSE: <soap:Envelope ...><soap:Body><AddResponse ...><AddResult>8</AddResult>...
RESULT = 8
```

The result `8` is the sum of the two hard-coded inputs (`intA=5`, `intB=3`).

### Open in Kaoto

You can open `soap-sample.camel.yaml` directly in the [Kaoto VS Code extension](https://marketplace.visualstudio.com/items?itemName=redhat.vscode-kaoto) to visualise and edit the route graphically.

## How It Works

The route performs these steps:

1. **`setHeaders`** — sets `Content-Type: text/xml; charset=utf-8` and `SOAPAction: http://tempuri.org/Add`.
2. **`setBody`** — inlines the SOAP envelope requesting `Add(5, 3)`.
3. **`to: http://...`** — sends the POST request to the remote SOAP endpoint.
4. **`log`** — prints the raw XML response.
5. **`setBody` (XPath)** — extracts the `AddResult` text node from the response.
6. **`log`** — prints the numeric result.

## Project Structure

```text
soap-request/
└── soap-sample.camel.yaml    # One-shot route (once → HTTP SOAP → XPath → log)
```
