# Adding a test template

This project allows you to dynamically test the parsing of documents.
You need to add a test template by following the following steps.

## Correct test template

To add a correct test template, please follow these steps:

- Go into src/test/resources/document/correct
- Create a new folder with the name of the test
- Create a new file named document.yaml which contains the content of the document to test
- Create a new file named expected-records.csv which contains the expected records after parsing.
```text
single-value-key;value
multiple-values-key;first-value;second-value;third-value
```
- Add an enum entry with the same name as the folder you created inside src/test/java/io/github/sekelenao/smallyaml/test/util/constant/CorrectTestDocument.java

## Incorrect test template

- Go into src/test/resources/document/incorrect
- Create a new folder with the name of the test
- Create a new file named document.yaml which contains the content of the document to test
- Create a new file named expected-exception.csv which contains the expected exception after parsing
```text
io.github.sekelenao.smallyaml.api.exception.MostSpecificException;Message of the exception
```
- Add an enum entry with the same name as the folder you created inside src/test/java/io/github/sekelenao/smallyaml/test/util/constant/IncorrectTestDocument.java