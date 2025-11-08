<h1 align="center">Using the Java API</h1>

## All the steps

- Create a `LineProvider`
- Create a `Document` by passing the `LineProvider`
- Use the `Document`

## Creating a `LineProvider`

A LineProvider is an interface that provides lines. This library provides a few static factories
to create a `LineProvider` from common sources:

- `LineProvider.with(String text)`
- `LineProvider.with(BufferedReader bufferedReader)`
- `LineProvider.with(InputStream inputStream)`
- `LineProvider.with(InputStream inputStream, Charset charset)`

You can also create your own implementation of `LineProvider`. The interface is not very complex.


> [!WARNING]  
> If you implement your own `LineProvider`, consider overriding the `close()` method to release any system resources
> or perform cleanup operations.

## Creating a `Document`

The only implementation for the moment is the `PermissiveDocument`. To create a `Document`, you need to pass a
`LineProvider` to the factory method `PermissiveDocument.from(LineProvider lineProvider)`.

## Using the `Document`

The `Document` interface provides a few methods to access the properties.

To retrieve a single value, use the `getSingle<type>()` methods and for multiple values, use the `getMultiple<type>()` methods.

Methods are available for primitive types.

It is an immutable class, so you can use it safely in a multithreaded environment.

> [!WARNING]  
> If a key has no value attached, then it does not exist for SmallYAML.

## Examples

### Reading single and multiple properties as String

```yaml
example:
    single: From a String
    multiple:
        - first
        - last
```

```java
try (var provider = LineProvider.with(yaml)){
    var document = PermissiveDocument.from(provider);
    String single = document.getSingleString("example.single").orElseThrow();
    List<String> multiple = document.getMultipleStrings("example.multiple").orElseThrow();
    System.out.println(single);
    System.out.println(multiple);
}
```

```text
From a String
[first, last]
```

### Mapping a property to a custom type

```yaml
example:
  URI: "mailto:user@example.com"
```

```java
try (var provider = LineProvider.with(yaml)){
    var document = PermissiveDocument.from(provider);
    java.net.URI uri = document.getSingle("example.URI", URI::create).orElseThrow();
    System.out.println(uri);
}
```

```text
mailto:user@example.com
```

### Iterate over not fixed properties

```yaml
applications:
    my-springboot-starter:
        port: 8080
    my-angular-front-app:
        port: 4200
```

```java
try (var provider = LineProvider.with(yaml)){
    var document = PermissiveDocument.from(provider);
    for (String key : document.subKeysOf("applications")){
        System.out.println("Name of the application: " + key);
        int port = document.getSingleInt(key + ".port").orElseThrow();
        System.out.println("Port: " + port);
    }
}
```

```text
Name of the application: applications.my-angular-front-app
Port: 4200
Name of the application: applications.my-springboot-starter
Port: 8080
```
