<h1 align="center">Using the Java API</h1>

SmallYAML provides two ways to work with YAML documents:
- **Permissive Mode**: Quick and easy, similar to a `Map<String, Object>`. No predefined schema required.
- **Bounded Mode**: Enforces a schema with mandatory and optional properties. Ideal for robust configuration management.

---

## Common concepts

### Key case-insensitivity

SmallYAML keys are **case-insensitive**. Whether you use `PermissiveMode` or `BoundedMode`, keys like `Server.Port`, `server.port`, and `SERVER.PORT` are considered identical.

> [!IMPORTANT]
> Internally, SmallYAML converts all keys to **lowercase**. 
> - When defining a `PropertyIdentifier`, any case can be used, but it will be stored in lowercase.
> - When using `PermissiveDocument`, you **must** use lowercase keys in `get` methods 
(e.g., `document.getSingleString("server.port")`), as the parser stores them in lowercase.

### The `LineProvider`

Regardless of the mode you choose, you first need a `LineProvider`. It's the source of your YAML content.
SmallYAML provides factories for common sources:

- `LineProvider.with(String text)`
- `LineProvider.with(BufferedReader reader)`
- `LineProvider.with(InputStream inputStream)`
- `LineProvider.with(InputStream inputStream, Charset charset)`

> [!TIP]
> `LineProvider` implements `AutoCloseable`. Always use it in a try-with-resources block.

### The `Document` interface

Both `PermissiveDocument` and `BoundedDocument` implement the `Document` interface.
It allows you to iterate over all properties:

```java
for (Property<?> property : document) {
    System.out.println(property.key() + " = " + property.value());
}

// Or using Java Streams
document.stream()
    .filter(p -> p.key().startsWith("app."))
    .forEach(System.out::println);
```

---

## 1. Permissive Mode (`PermissiveDocument`)

Use this when you don't want to define a schema or when you're dealing with dynamic keys.

### Creating a `PermissiveDocument`

```java
try (var provider = LineProvider.with(yaml)) {
    var document = PermissiveDocument.from(provider);
    // use document...
}
```

### Accessing properties

Properties are accessed via dot-notation (e.g., `server.port`).

- **Strings**: `getSingleString(key)`, `getMultipleStrings(key)`
- **Primitives**: `getSingleInt(key)`, `getMultipleInts(key)`, `getSingleBooleanOrThrow(key)`, etc.
- **Custom Mapping**: `getSingle(key, mapper)`, `getMultiple(key, mapper)`

```java
Optional<String> name = document.getSingleString("user.name");
int port = document.getSingleInt("server.port").orElse(8080);
List<URI> endpoints = document.getMultiple("endpoints", URI::create).orElse(List.of());
```

### Dynamic Keys

You can discover keys at a certain level using `subKeysOf(String key)`.

```java
// yaml
// apps:
//   app1: { port: 80 }
//   app2: { port: 81 }

for (String appKey : document.subKeysOf("apps")) {
    int port = document.getSingleInt(appKey + ".port").orElseThrow();
}
```

---

## 2. Bounded Mode (`BoundedDocument`)

Use this for structured configurations where you want to ensure all required fields are present and typed correctly.

### Defining your Schema

First, define your properties using `PropertyIdentifier`. There are 4 types:
- `SingleMandatoryIdentifier`
- `SingleOptionalIdentifier`
- `MultipleMandatoryIdentifier`
- `MultipleOptionalIdentifier`

It's common practice to define them as `static final` fields:

```java
public class Config {
    public static final SingleMandatoryIdentifier PORT = SingleMandatoryIdentifier.define("server.port");
    public static final SingleOptionalIdentifier HOST = SingleOptionalIdentifier.define("server.host");
    public static final MultipleMandatoryIdentifier ADMINS = MultipleMandatoryIdentifier.define("security.admins");
}
```

### Creating the Factory

A `BoundedDocumentFactory` is built using a builder. You can register identifiers manually or by scanning classes.

```java
var factory = BoundedDocument.factoryBuilder()
    .scan(Config.class) // Automatically finds all PropertyIdentifier fields
    .buildFactory();
```

### Parsing the Document

```java
try (var provider = LineProvider.with(yaml)) {
    BoundedDocument doc = factory.createDocument(provider);
    
    // Values are directly accessible and typed
    int port = doc.getInt(Config.PORT); // returns int
    Optional<String> host = doc.get(Config.HOST); // returns Optional<String>
    List<String> admins = doc.get(Config.ADMINS); // returns List<String>
}
```

### Handling Unknown Properties

By default, unknown properties in the YAML are ignored. You can change this:

```java
var factory = BoundedDocument.factoryBuilder()
    .scan(Config.class)
    .unknownPropertyConsumer((key, value) -> {
        System.err.println("Warning: unknown property " + key + " = " + value);
    })
    .buildFactory();
```

---

## Summary of Differences

| Feature          | `PermissiveDocument`      | `BoundedDocument`                 |
|------------------|---------------------------|-----------------------------------|
| **Schema**       | None (ad-hoc)             | Explicitly defined                |
| **Validation**   | Manual                    | Automatic (at parse time)         |
| **Return Types** | Mostly `Optional`         | Direct types for Mandatory        |
| **Unknown Keys** | Always kept               | Configurable (ignored by default) |
| **Use Case**     | Prototyping, dynamic data | Production-ready config           |
