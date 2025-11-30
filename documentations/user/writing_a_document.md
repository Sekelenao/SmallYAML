<h1 align="center">Writing a SmallYAML document</h1>

## What is YAML?

YAML is a document format used to store properties. Think of it as a settings file organized hierarchically.

The goal is to access the value of these properties using a path.

For more information, please check: https://yaml.org/

> [!IMPORTANT]  
> SmallYAML is an opinionated and simplified Java library that only supports a subset of the complete YAML syntax to
> ensure predictability and simplicity.

## SmallYAML syntax

You can define properties using a simple key-value format. You must follow these rules:

- Keys are composed of alphanumeric characters, underscores, and dashes. A key cannot start or end with a dash/underscore.
- A path leading to a value must be unique. All keys are case-insensitive.
- Keys must have space(s) after the colon `:`.
- A list value must have space(s) after the dash `-`.
- You don't need to escape quotes inside quotes.
- Idents must be spaces (U+0020).

To define a child property, indent the key with more spaces than the parent key. You can choose the number of spaces
you want to use.

You can attach multiple values to a single key using the dash `-` operator. Each value must have its own line.

> [!TIP]
> You can inline a path using the dot `.` operator and the two formats can be mixed.

#### Indented child property

```yaml
parent:
  child: value
```
#### Inline path

```yaml
parent.child: value
```

#### Mix of the two formats

```yaml
one:
  two.three: value
```

#### Multiple values for a key

```yaml
key:
  - value1
  - value2
```