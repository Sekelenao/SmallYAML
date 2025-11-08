<h1 align="center">User guide</h1>

## What is YAML?

YAML is a document format used to store properties. Think of it as a settings file organized hierarchically.

The goal is to access the value of these properties using a path.

> [!IMPORTANT]  
> SmallYAML is an opinionated and simplified Java library that only supports a subset of the complete YAML syntax to
> ensure predictability and simplicity.

> [!NOTE]  
> For more information, please check: https://yaml.org/

## Writing a SmallYAML document

### Defining properties

You can define properties using a simple key-value format.

> [!IMPORTANT]
> Keys are composed of alphanumeric characters, underscores, and dashes. A key cannot start or end with a dash/underscore.

> [!CAUTION]  
> A path leading to a value must be unique. All keys are case-insensitive.

To define a child property, indent the key with more spaces than the parent key. You can choose the number of spaces
you want to use.

> [!CAUTION]  
> Idents must be spaces (U+0020).

```yaml
parent:
  child: value
```



> [!IMPORTANT]  
> A value must be quoted if it contains one of the following characters: `:` `-` `"`.
> You must escape quotes using the following notation `\"`.
> A single backslash not preceding quotes does not require escaping.

```yaml
special-value1: ":"
special_value2: "-"
special-value3: "\""
```

> [!TIP]
> You can inline a path using the dot `.` operator.
> For example, you can access the two following values using the same path `parent.child`

```yaml
parent.child: value
```

> [!TIP]
> You can mix the two formats in the same path.

```yaml
one:
  two.three: value
```

You can attach multiple values to a single key using the dash `-` operator. Each value must have its own line.

```yaml
key:
  - value1
  - value2
```