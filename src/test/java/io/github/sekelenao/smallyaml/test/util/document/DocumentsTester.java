package io.github.sekelenao.smallyaml.test.util.document;

import io.github.sekelenao.skcsv.SkCsv;
import io.github.sekelenao.smallyaml.api.document.Document;
import io.github.sekelenao.smallyaml.api.document.property.MultipleValuesProperty;
import io.github.sekelenao.smallyaml.api.document.property.SingleValueProperty;
import io.github.sekelenao.smallyaml.api.line.provider.BufferedReaderLineProvider;
import io.github.sekelenao.smallyaml.test.util.constant.CorrectTestDocument;
import io.github.sekelenao.smallyaml.test.util.document.property.PropertyType;
import io.github.sekelenao.smallyaml.test.util.document.property.PropertyTypeCounter;
import io.github.sekelenao.smallyaml.test.util.resource.TestResource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class DocumentsTester<D extends Document> {

    private final DocumentProvider<D> documentProvider;

    public DocumentsTester(DocumentProvider<D> documentProvider) {
        this.documentProvider = Objects.requireNonNull(documentProvider);
    }

    private static PropertyTypeCounter amountOfPropertyTypeFor(Document document){
        Objects.requireNonNull(document);
        var propertyTypeCounter = new PropertyTypeCounter();
        for (var iterator = document.iterator(); iterator.hasNext(); ) {
            var property = iterator.next();
            switch (property){
                case SingleValueProperty ignored -> propertyTypeCounter.encountered(PropertyType.SINGLE);
                case MultipleValuesProperty ignored -> propertyTypeCounter.encountered(PropertyType.MULTIPLE);
                default -> throw new IllegalStateException("Unexpected value: " + property);
            }
        }
        return propertyTypeCounter;
    }
    
    public void testWithAllCorrectDocuments(
        DocumentSingleStringGetter<D> documentSingleStringGetter, DocumentStringListGetter<D> documentStringListGetter
    ) throws IOException, URISyntaxException {

        Objects.requireNonNull(documentSingleStringGetter);
        Objects.requireNonNull(documentStringListGetter);
        for (var documentResource: CorrectTestDocument.values()){
            var bufferedReader = Files.newBufferedReader(TestResource.find(documentResource));
            var csvPath = TestResource.find(documentResource.csvResourcePath());
            var expectedRecordsCsv = SkCsv.from(csvPath);
            try(var bufferedReaderLineProvider = new BufferedReaderLineProvider(bufferedReader)) {
                var document = documentProvider.from(bufferedReaderLineProvider);
                var logMessage = String.format("Testing correct document '%s' with %s", documentResource, document.getClass());
                System.out.println(logMessage);
                var propertyTypeInCsvCounter = new  PropertyTypeCounter();
                for (var line : expectedRecordsCsv){
                    var key = line.getFirst();
                    int size = line.size();
                    if (size < 2) {
                        throw new IllegalArgumentException("Expected records CSV is invalid: " + csvPath.toAbsolutePath());
                    } else if (size == 2) {
                        propertyTypeInCsvCounter.encountered(PropertyType.SINGLE);
                        var expectedValue = line.get(1);
                        assertEquals(expectedValue, documentSingleStringGetter.get(document, key));
                    } else {
                        propertyTypeInCsvCounter.encountered(PropertyType.MULTIPLE);
                        var expectedValue = line.stream().skip(1).toList();
                        assertEquals(expectedValue, documentStringListGetter.get(document, key));
                    }
                }
                var propertyTypeInDocument = amountOfPropertyTypeFor(document);
                assertEquals(propertyTypeInCsvCounter, propertyTypeInDocument);
            }
        }

    }

}
