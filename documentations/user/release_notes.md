<h1 align="center">Release Notes</h1>

## Installation

### Maven

```xml
<dependency>
    <groupId>io.github.sekelenao</groupId>
    <artifactId>small-yaml</artifactId>
    <version>0.2.0</version>
</dependency>
```

### Gradle

```groovy
implementation("io.github.sekelenao:small-yaml:0.2.0")
```

## Compatibility matrix

| Java version | SmallYAML version |
|--------------|-------------------|
| 21+          | 0.1.x, 0.2.x      |

## All Versions

### 0.2.0

#### Added

- `BoundedDocument` for schema-enforced YAML parsing
- `PropertyIdentifier` system to define cardinality (SINGLE/MULTIPLE) and presence (MANDATORY/OPTIONAL)
- `BoundedDocumentFactoryBuilder` for fluent schema configuration
- Automatic registration of `PropertyIdentifier` fields via reflection
- Custom handling of unknown properties with `UnknownPropertyConsumer`
- `Document` interface now implements `Iterable<Property<?>>`
- `Document` interface now provides `stream()` and `spliterator()` support

### 0.1.0

#### Added

- SmallYAML parsing
- LineProvider API can read from files, strings, or network streams
- PermissiveDocument abstraction with primitive support and strong typing
- Java 21+ support
- Maven Central availability
