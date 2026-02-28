<h1 align="center">Writing a SmallYAML document</h1>

## What is YAML?

YAML is a document format used to store properties. Think of it as a settings file organized hierarchically.

The goal is to access the value of these properties using a path.

For more information, please check: https://yaml.org/

> [!IMPORTANT]  
> SmallYAML is an opinionated and simplified Java library that only supports a subset of the complete YAML syntax to
> ensure predictability and simplicity.

## Syntax rules

### Keys

- **Composition**: Keys are composed of alphanumeric characters, underscores (`_`), and dashes (`-`).
- **Formatting**: A key cannot start or end with a dash or an underscore.
- **Case-insensitivity**: Keys are **case-insensitive** (e.g., `Server.Port` is the same as `server.port`).
- **Path uniqueness**: A path leading to a value must be unique within the document.
- **Colon**: Each key must be followed by a colon `:`. 
- **Spacing**: There must be at least one space (U+0020) after the colon if a value is present on the same line.

### Values

- **Trimming**: Leading and trailing whitespaces are automatically removed from values.
- **Quotes**: If a value starts and ends with double quotes (`"`), they are automatically removed (e.g., `"my value"` becomes `my value`). You don't need to escape quotes inside a quoted value.
- **Data Types**: All values are initially parsed as strings. The Java API provides methods to convert them into primitives (int, boolean, etc.) or custom types.

### Hierarchy & Indentation

SmallYAML uses indentation to represent structure.

- **Spaces only**: You MUST use spaces (U+0020) for indentation. Tabs are forbidden.
- **Relative indentation**: To define a child property, indent its key with more spaces than the parent key.

```yaml
parent:
  child: value
```

- **Inline path**: You can use the dot `.` operator to define a path on a single line.

```yaml
parent.child: value
```

### Lists

You can attach multiple values to a single key using the dash `-` operator.

- **Dash spacing**: There must be at least one space (U+0020) after the dash.
- **One per line**: Each value in a list must be on its own line.
- **Constraints**: Nested objects inside lists are NOT supported.

```yaml
key:
  - value1
  - value2
```

### Comments

You can add comments to your document using the hash `#` symbol.

- **Whole line**: A line starting with `#` is ignored.
- **Inside values**: If a `#` is placed in a value (on the same line as a key or a list dash), it is considered **part of the value**. End-of-line comments are NOT supported.

```yaml
# This is a comment
server.port: 8080 # This is NOT a comment, it's part of the value
```