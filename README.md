<p align="center">
  <img src=".idea/icon.svg" width="300" alt="logo">
</p>

<h2 align="center">
Simplified YAML library with limited syntax support and built-in validation
</h2>

[![Coverage](./badges/jacoco.svg)](https://github.com/Sekelenao/SmallYAML/actions/workflows/maven.yml)
[![Branches](./badges/branches.svg)](https://github.com/Sekelenao/SmallYAML/actions/workflows/maven.yml)

## Overview

SmallYAML is a simplified YAML library that prioritizes maintainability and predictability over comprehensive YAML
specification coverage. Rather than supporting the full YAML 1.2 specification, SmallYAML intentionally constrains the
allowed syntax to a carefully chosen subset that eliminates common sources of configuration complexity and parsing
ambiguity.

## Roadmap

### Version 1.0.0

- [ ] YAML parsing and loading

### Version 1.1.0

- [ ] Bounded documents
- [ ] Deprecation warnings of properties in bounded documents

### Research & Exploration

- Merging documents with override policies
- Automatic casting for bounded documents at the start for safe loading
- Automatic document template generation for bounded documents

