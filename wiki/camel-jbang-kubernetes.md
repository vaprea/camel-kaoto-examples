# Camel JBang Kubernetes

The Camel Kubernetes plugin lets you deploy Camel routes to Kubernetes directly from the CLI, covering the full lifecycle: export, build, push, and deploy.

Official documentation: [camel-jbang-kubernetes](https://camel.apache.org/manual/camel-jbang-kubernetes.html)

## Prerequisites

- `kubectl` installed and configured
- A running Kubernetes cluster (local or remote)

## Setup

```bash
camel plugin add kubernetes
camel plugin get
camel kubernetes --help
```

## Commands

### Export

Generates a Maven/Gradle project ready for Kubernetes deployment:

```bash
camel kubernetes export route.yaml --dir some/path/to/project
```

Default runtime is Quarkus. Override with `--runtime=quarkus`, `--runtime=springboot`, or `--runtime=camel-main`.

### Run

Combines export, build, push, and deploy into a single step:

```bash
camel kubernetes run route.yaml --image-registry=kind
```

Use `--dev` for hot-reload during development.

### Logs

```bash
camel kubernetes logs --name=route
```

### Delete

```bash
camel kubernetes delete --name=route
```

## Export / Run Options

| Option | Description |
|--------|-------------|
| `--service-account` | Runtime service account |
| `--dependency` | Add Maven/Camel dependencies |
| `--build-property` | Maven/Gradle build properties |
| `--property` | Runtime properties or property files |
| `--config` | Mount ConfigMap/Secret as config |
| `--resource` | Mount ConfigMap/Secret as resource |
| `--env` | Environment variables |
| `--volume` | Volume mounts |
| `--image-registry` | Container registry location |
| `--image-builder` | Build tool: `docker`, `jib`, `s2i` |
| `--cluster-type` | Target platform: `openshift`, `minikube`, `kind` |

## Traits

Traits customize the generated Kubernetes manifests. Configuration is resolved in this priority order:

1. `--trait` command-line options
2. Annotations with `trait.camel.apache.org/*` prefix
3. Profile-specific properties (`application-<profile>.properties`)
4. Default properties (`application.properties`)

### Container

```bash
camel kubernetes export Sample.java \
  --trait container.name=my-container \
  --trait container.port=8088 \
  --trait container.request-cpu=0.005 \
  --trait container.request-memory=100Mi
```

### Environment

```bash
camel kubernetes export Sample.java --env MY_VAR=my-value
```

### Mount

```bash
camel kubernetes export Sample.java \
  --trait mount.configs=configmap:my-data \
  --trait mount.volumes=my-pvc:/container/path
```

### Service

Exposes the application via a Kubernetes Service. Supports `ClusterIP`, `NodePort`, and `LoadBalancer` types.

### Ingress

```bash
camel kubernetes export Sample.java --trait ingress.enabled=true
```

### CronJob

```bash
camel kubernetes export Sample.java \
  --trait cronjob.enabled=true \
  --trait cronjob.schedule="* * * * 2"
```

### Knative

Enable serverless workloads and eventing integration:

```bash
camel kubernetes export Sample.java --trait knative-service.enabled=true
```

Knative component routes automatically generate Triggers, Subscriptions, and Sink Bindings.

## Runtime Configuration

### Properties

```bash
camel kubernetes run route.yaml --property my-key=my-val
camel kubernetes run route.yaml --property /path/to/file.properties
```

### ConfigMaps and Secrets

```bash
camel kubernetes run route.java --config configmap:game-config
camel kubernetes run route.java --resource configmap:my-data@/etc/custom-path
```

Mounted paths inside the container:

| Source | Config path | Resource path |
|--------|-------------|---------------|
| ConfigMap | `/etc/camel/conf.d/_configmaps/<name>/` | `/etc/camel/resources.d/_configmaps/<name>/` |
| Secret | `/etc/camel/conf.d/_secrets/<name>/` | `/etc/camel/resources.d/_secrets/<name>/` |

## Labels and Annotations

```bash
camel kubernetes export Sample.java \
  --annotation project.team=camel-experts \
  --label app-version=1.0
```

The label `camel.apache.org/integration` is set to the project name by default.

## Health Probes

The Observability Services component (enabled by default) exposes health endpoints:

| Runtime | Liveness | Readiness | Startup |
|---------|----------|-----------|---------|
| Quarkus | `/observe/health/live` | `/observe/health/ready` | `/observe/health/started` |
| Spring Boot | `/observe/health/liveness` | `/observe/health/readiness` | — |

## OpenAPI Specifications

The plugin can generate routes from an OpenAPI specification and deploy them to Kubernetes.

Reference: [OpenAPI Specifications](https://camel.apache.org/manual/camel-jbang-kubernetes.html#_openapi_specifications)

Export a route backed by an OpenAPI spec:

```bash
camel kubernetes export my-api.yaml --open-api openapi.json --dir some/path/to/project
```

The spec is packaged with the generated project and mounted into the container at runtime. The route uses the `rest-openapi` component to serve and validate requests against the spec.

## Minikube

Recommended setup (Camel 4.10+):

```bash
minikube start --addons registry --driver=docker
eval $(minikube -p minikube docker-env)
camel kubernetes run demo.camel.yaml \
  --cluster-type=minikube \
  --build-property=quarkus.kubernetes.image-pull-policy=Never \
  --image-registry "$(kubectl -n kube-system get service registry -o jsonpath='{.spec.clusterIP}')" \
  --image-builder=docker
```

If the registry addon is unavailable, use the Docker build strategy to build directly into minikube's daemon (no registry needed):

```bash
eval $(minikube docker-env)
camel kubernetes run demo.camel.yaml \
  --cluster-type=minikube \
  --build-property=quarkus.kubernetes.image-pull-policy=Never \
  --build-property=quarkus.container-image.builder=docker
```

## OpenShift

```bash
camel kubernetes export Sample.java --cluster-type=openshift
```

Enables OpenShift Routes, ImageStream, BuildConfig, and S2I builder by default. Override the builder with `--image-builder=jib` if needed.

## Build and Deploy with Maven

Build container image:

```bash
./mvnw package -Dquarkus.container-image.build=true
```

Build and deploy:

```bash
./mvnw package -Dquarkus.kubernetes.deploy=true
```

Kubernetes manifests are generated at `src/main/kubernetes/kubernetes.yml`.

## Check Deployment Status

```bash
# All resources in the default namespace
kubectl get all

# Pod logs
kubectl logs deployment/<name> -f

# Port-forward a ClusterIP service for local testing
kubectl port-forward svc/<name> 8080:80
```
