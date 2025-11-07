<h1 align="center">User guide</h1>

## What is YAML?

YAML is a document format used to store properties. Think of it as a settings file organized hierarchically, 
similar to a .properties file but with more structure.

For more information, please check: https://yaml.org/

SmallYAML is a simplified Java library that only supports a subset of the complete YAML syntax to ensure predictability 
and simplicity.

## Writing a SmallYAML document


1. Simple Properties (Key-Value)

The simplest form is a key followed by a colon and a value BufferedReaderLineProviderTest.java:37 :

application.name: MyApp  
server.port: 8080  
database.host: localhost

Each line defines a property. The key is on the left of the colon, the value on the right.
2. Hierarchy with Indentation

You can organize properties hierarchically using indentation (spaces, not tabs) InputStreamLineProviderTest.java:46-56 :

application:  
name: MyApp  
version: 1.0.0  
database:  
host: localhost  
port: 5432

This is equivalent to application.name, application.version, database.host, and database.port.
3. Value Lists

To define multiple values for a single key, use the dash - InputStreamLineProviderTest.java:53-55 :

servers:  
- production.example.com  
- staging.example.com  
- development.example.com

Each line with a dash represents a list element.
4. Comments

Lines starting with # are comments and are ignored BufferedReaderLineProviderTest.java:36-39 :

# Application configuration
application:  
name: MyApp  # Application name

Complete Example

# Application configuration
application:  
name: ConfigService  
version: 2.1.0

# Server configuration
server:  
host: 0.0.0.0  
port: 8080  
ssl-enabled: true

# Database configuration
database:  
urls:  
- jdbc:postgresql://db1:5432/config  
- jdbc:postgresql://db2:5432/config  
connection-pool:  
min-size: 5  
max-size: 20

# List of enabled features
features:  
- authentication  
- authorization  
- logging

Important Rules

    Indentation: Always use spaces (not tabs) for indentation
    Colons: There must be a space after the colon in key: value
    Dashes for lists: Each list item starts with - (dash followed by a space)
    No quotes needed: Text values generally don't need quotes
    Unique keys: Each key can only appear once

Supported Value Types

    Text: name: MyApp
    Integers: port: 8080 PermissiveDocumentTest.java:263-269
    Decimals: threshold: 20.20 PermissiveDocumentTest.java:354
    Booleans: enabled: true or enabled: false (case-sensitive)

Notes

SmallYAML is a simplified Java library that only supports a subset of the complete YAML syntax README.md:31-34 . It prioritizes simplicity and predictability over complete YAML 1.2 specification coverage. Advanced YAML features (anchors, aliases, complex types) are not supported.

