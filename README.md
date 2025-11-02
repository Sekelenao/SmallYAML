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

## Versions

### Version 1.0.0 (Not released)

- [x] YAML parsing and loading

### Benchmarks

Benchmarks are run on the following file: `src/test/resources/document/correct/huge_document/document.yaml`

The test is to parse the file and load it into a `PermissiveDocument`

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

## Overview

SmallYAML is a simplified YAML library that prioritizes maintainability and predictability over comprehensive YAML
specification coverage. Rather than supporting the full YAML 1.2 specification, SmallYAML intentionally constrains the
allowed syntax to a carefully chosen subset that eliminates common sources of configuration complexity and parsing
ambiguity.

### Futur ideas

- Validator to ensure config is correct before PROD deployment
- Bounded documents
- Deprecation warnings of properties in bounded documents
- Merging documents with override policies
- Automatic document template generation for bounded documents

