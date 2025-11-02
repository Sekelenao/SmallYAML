<p align="center">
  <img src=".idea/icon.svg" width="300" alt="logo">
</p>

<h2 align="center">
Simplified YAML library with limited syntax support and built-in validation
</h2>

### Status: No stable release yet

[![Java](https://img.shields.io/badge/Java_21%2B-%23ED8B00.svg?logo=openjdk&logoColor=white)](https://docs.oracle.com/en/java/javase/21/docs/api/index.html)
![Tests](https://raw.githubusercontent.com/Sekelenao/SmallYAML/badges/Tests.svg)
![Coverage](https://raw.githubusercontent.com/Sekelenao/SmallYAML/badges/Coverage.svg)
![Branches](https://raw.githubusercontent.com/Sekelenao/SmallYAML/badges/Branches.svg)

## Overview

SmallYAML is a simplified YAML library that prioritizes maintainability and predictability over comprehensive YAML
specification coverage. Rather than supporting the full YAML 1.2 specification, SmallYAML intentionally constrains the
allowed syntax to a carefully chosen subset that eliminates common sources of configuration complexity and parsing
ambiguity.

## Versions

### Version 1.0.0 (Not released)

- [x] YAML parsing and loading

### Benchmarks

We leverage the Java Microbenchmark Harness (JMH) to ensure the optimal performance of our parsing mechanisms.

Our benchmarks specifically focus on evaluating the efficiency of loading a large configuration file,
located at `src/test/resources/document/correct/huge_document/document.yaml`, into a `PermissiveDocument` object.

Each benchmark run is executed in a dedicated JVM instance, configured with an initial heap size of 1GB and a maximum
heap size of 1GB (`-Xms1g`, `-Xmx1g`), to provide a controlled and consistent environment.

We use a rigorous testing methodology, beginning with five warmup iterations (each lasting 2 seconds) to allow the JVM
to perform its optimizations, followed by 20 measurement iterations (also 2 seconds each).

Results are then reported as the average time taken per operation, expressed in milliseconds, providing clear and
actionable performance metrics.

#### Snapshot 1.0.0

| Mode | Cnt | Score   | Error   | Units |
|------|-----|---------|---------|-------|
| avgt | 20  | 191,963 | ± 2,617 | ms/op |

#### Snapshot 1.0.1

| Mode | Cnt | Score   | Error   | Units |
|------|-----|---------|---------|-------|
| avgt | 20  | 167,980 | ± 2,320 | ms/op |

### What am I working on right now?

- Testing the API in Simple projects and Spring projects
- Adding javadocs
- Adding documentation

### Futur ideas

- Validator to ensure config is correct before PROD deployment
- Bounded documents
- Deprecation warnings of properties in bounded documents
- Merging documents with override policies
- Automatic document template generation for bounded documents
