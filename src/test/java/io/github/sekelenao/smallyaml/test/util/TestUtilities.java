package io.github.sekelenao.smallyaml.test.util;

import io.github.sekelenao.smallyaml.api.document.SmallYAMLDocument;
import io.github.sekelenao.smallyaml.api.document.property.MultipleValuesProperty;
import io.github.sekelenao.smallyaml.api.document.property.SingleValueProperty;
import io.github.sekelenao.smallyaml.test.util.resource.TestResource;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class TestUtilities {

    private static final int MAX = 10_000;

    private static final String[] BLANK_CHARS = {" ", "\t"};

    private static final Random RANDOM = new Random();

    public static String generateBlankString(int size){
        var builder = new StringBuilder();
        for(int i = 0; i < size; i++){
            builder.append(BLANK_CHARS[RANDOM.nextInt(BLANK_CHARS.length)]);
        }
        return builder.toString();
    }

    public static Stream<Integer> intProvider() {
        return Stream.generate(() -> RANDOM.nextInt(MAX)).limit(50);
    }

    public static Path findResource(TestResource testResource) throws URISyntaxException {
        Objects.requireNonNull(testResource);
        var url = TestUtilities.class.getClassLoader().getResource(testResource.path());
        if(url == null){
            throw new IllegalArgumentException("Resource not found: " + testResource.path());
        }
        return Path.of(url.toURI());
    }

    public static void checkAmountOfEachPropertyType(SmallYAMLDocument document, int single, int multiple){
        Objects.requireNonNull(document);
        int amountOfSingleValueProperties = 0;
        int amountOfMultipleValuesProperties = 0;
        for (var iterator = document.iterator(); iterator.hasNext(); ) {
            var property = iterator.next();
            switch (property){
                case SingleValueProperty ignored -> amountOfSingleValueProperties++;
                case MultipleValuesProperty ignored -> amountOfMultipleValuesProperties++;
                default -> throw new IllegalStateException("Unexpected value: " + property);
            }
        }
        assertEquals(single, amountOfSingleValueProperties);
        assertEquals(multiple, amountOfMultipleValuesProperties);
    }

}
