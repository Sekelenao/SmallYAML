<p align="center">
  <img src=".idea/icon.svg" width="300" alt="logo">
</p>

<h2 align="center">
Simplified YAML library with limited syntax support and built-in validation
</h2>

### Status: No stable release yet

[![Java](https://img.shields.io/badge/Java_21%2B-%23ED8B00.svg?logo=openjdk&logoColor=white)](https://docs.oracle.com/en/java/javase/21/docs/api/index.html)
![Coverage](https://raw.githubusercontent.com/Sekelenao/SmallYAML/badges/Coverage.svg)
![Branches](https://raw.githubusercontent.com/Sekelenao/SmallYAML/badges/Branches.svg)

## Versions

### Version 1.0.0 (Not released)

- [ ] YAML parsing and loading

### What am I working on right now?

- Thinking about providing default PropertyValueMappers
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
- Automatic casting for bounded documents at the start for safe loading
- Automatic document template generation for bounded documents

