<p align="center">
  <img src=".idea/icon.svg" width="300" alt="logo">
</p>

<h2 align="center">
Predictability is a Feature. Cut the noise, keep the functionality.
</h2>

[![Java](https://img.shields.io/badge/Java_21%2B-%23ED8B00.svg?logo=openjdk&logoColor=white)](https://docs.oracle.com/en/java/javase/21/docs/api/index.html)
![Tests](https://raw.githubusercontent.com/Sekelenao/SmallYAML/badges/Tests.svg)
![Coverage](https://raw.githubusercontent.com/Sekelenao/SmallYAML/badges/Coverage.svg)
![Branches](https://raw.githubusercontent.com/Sekelenao/SmallYAML/badges/Branches.svg)

## Overview

SmallYAML is a simplified and opinionated YAML library that prioritizes maintainability and predictability over 
comprehensive YAML specification coverage. Rather than supporting the full YAML specification, SmallYAML intentionally 
constrains the allowed syntax to a carefully chosen subset that eliminates common sources of configuration complexity 
and parsing ambiguity.

To increase the predictability of the library:

- The parsing process operates without backtracking. It strives to encounter each char only one time.
- Documents are immutable to be used in a multithreaded environment.
- Keys are case-insensitive and restrained to alphanumeric characters, underscores, and dashes.
- Nested objects in lists are forbidden.
- Null safety is enforced.
- Zero dependency strategy.
- If the version X.X.X is for Java 100+, it will be written in Java 100 and not Java 8.
- The library is fully tested.

## Table of contents

#### [How to write a SmallYAML document](documentations/user/writing_a_document.md)
#### [How to use the Java API](documentations/user/using_the_java_api.md)
#### [Release notes and compatibility](documentations/user/release_notes.md)
#### [Performance metrics](documentations/user/benchmarks.md)

## Hello user!

If you have any questions, please feel free to [open an issue](https://github.com/Sekelenao/SmallYAML/issues/new).

I love providing stuff that makes your life easier, so please consider supporting me by starring the project.

Thank you for using SmallYAML!