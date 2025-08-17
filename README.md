<p align="center">
  <img src=".idea/icon.svg" width="300" alt="logo">
</p>

<h2 align="center">
Simplified YAML library with limited syntax support and built-in config validation
</h2>

## Roadmap

### Version 1.0

- [ ] YAML parsing and loading
- [ ] Safe loading (condition checking, default value...)
- [ ] Deprecation warnings to keep configs simple
- 
### Research & Exploration

- Merging configs with override policies
- Static config loader
- Automatic casting at start for safe loading
- Automatic config template generation


### Overview

SmallYAML is a simplified YAML library designed with limited syntax support and built-in config validation. It provides a streamlined approach to YAML processing while maintaining essential functionality for most use cases.

### Limitations

While smallYAML covers the majority of YAML use cases, it has some intentional limitations to keep the syntax simple and maintainable:

#### 1. Values Must Be on the Same Line as Keys (Except for Lists)

```yaml
key: 
  "value on the other line" ❌
```
**✅ Supported:**
```yaml
key: "value on same line"
```

#### 2. No object lists support

SmallYAML does not provide support for lists containing objects or complex nested structures within arrays. 
This limitation helps simplify parsing and avoid syntactic ambiguities, promoting a more predictable and maintainable 
configuration structure.

```yaml
services:
  - id: "backend" ❌
    port: 8080
    env: "dev"
  - id: "backend" ❌
    port: 8080
    env: "prod"
```
**✅ Supported:**
```yaml
services:
  back-dev:
    id: "backend"
    port: 8080
    env: "dev"
  back-prod:
    id: "backend"
    port: 8080
    env: "prod"
```

#### 3. No Bracket-Style Lists

Flow sequences using square brackets are not supported. This enforces consistent block-style formatting.

``` yaml
items: [item1, item2, item3] ❌
numbers: [1, 2, 3, 4, 5] ❌
```
**✅ Supported:**
``` yaml
items:
  - item1
  - item2
  - item3
numbers:
  - 1
  - 2
  - 3
  - 4
  - 5
```

#### 4. No Inline Comments on Key Lines

Comments are not allowed on the same line as keys to maintain clean and readable configuration files.

```yaml
log:
  level: "INFO" # Change the log level here ❌
```
**✅ Supported:**
```yaml
log:
  # Change the log level here
  level: "INFO"
```



