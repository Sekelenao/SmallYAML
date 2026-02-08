# Testing Conventions

This document outlines the testing standards and best practices for the SmallYAML project. Following these guidelines ensures high code quality, maintainability, and consistent behavior across the codebase.

## Core Principles

1.  **One Test Class per Class**: Every source class should have a corresponding test class (e.g., `MyClass` -> `MyClassTest`).
2.  **API Testing is Mandatory**: Every class that is part of the public API must be tested.
3.  **High Coverage Target**: We aim for 100% code coverage on all API classes to ensure every logical path is verified.

## Best Practices

### Framework and Utilities
-   **JUnit 5**: Use the latest version of JUnit 5 for all tests.
-   **@DisplayName**: Always provide a descriptive name for test methods using the `@DisplayName` annotation to improve report readability.
-   **assertAll**: Use `assertAll` when performing multiple assertions within a single test case. This ensures that all assertions are executed even if one fails, providing a complete picture of the failure.
-   **ExceptionsTester**: Use the project's `ExceptionsTester` utility to verify that the correct exceptions are thrown with expected messages.

### Test Design
-   **Null Safety**: Systematically test for `null` inputs on methods that use `Objects.requireNonNull`.
-   **Method Isolation**: Ensure each test method focuses on a specific behavior or scenario.
-   **Comprehensive Verification**: If a test scenario involves a non-blocking failure or edge case (e.g., handling an unknown property), it must also verify that the rest of the system's state and primary functionality remains correct. Features should be tested as completely as possible within a given context.
-   **Feature Modularization & @Nested**:
    -   **Class Size**: If a class grows significantly by accumulating different features, consider splitting it into smaller, more focused classes. This promotes better maintainability and testability.
    -   **JUnit 5 @Nested**: For classes with many methods or distinct functional areas, use JUnit 5's `@Nested` annotation to group related tests. This creates a clear hierarchy and makes the test suite easier to navigate. See `PermissiveDocumentTest.java` for a reference on this pattern.
-   **Immutability and Side Effects**: Verify that operations do not have unexpected side effects and that immutability is respected where applicable.



-   **Package Alignment**: Test classes must reside in the same package as the source class (e.g., `src/main/java/com/example/Foo.java` -> `src/test/java/com/example/FooTest.java`).

### Data-Driven Testing
-   For testing the parser with various YAML inputs, refer to [Adding a test template](adding_test_template.md). This system allows for dynamic testing using YAML files and CSV expected results.

## Example Test Structure

```java
@DisplayName("MyComponent assertions")
final class MyComponentTest {

    @Nested
    @DisplayName("Initialization and safety")
    final class Lifecycle {

        @Test
        @DisplayName("Null safety checks")
        void nullChecks() {
            var component = new MyComponent();
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> component.doSomething(null)),
                () -> assertThrows(NullPointerException.class, () -> component.initialize(null, null))
            );
        }

    }

    @Nested
    @DisplayName("Core features")
    final class Features {

        @Test
        @DisplayName("Success scenario")
        void successScenario() {
            var component = new MyComponent();
            var result = component.process("input");
            assertEquals("expected", result);
        }

    }

}
```

