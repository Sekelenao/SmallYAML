package io.github.sekelenao.smallyaml.test.util;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;

public final class Tests {

    private static final int MAX = 10_000;

    private static final String[] BLANK_CHARS = {" ", "\t"};

    private static final Random RANDOM = new Random();

    public static String blankString(int size){
        var builder = new StringBuilder();
        for(int i = 0; i < size; i++){
            builder.append(BLANK_CHARS[RANDOM.nextInt(BLANK_CHARS.length)]);
        }
        return builder.toString();
    }

    public static Stream<Integer> intProvider() {
        return Stream.generate(() -> RANDOM.nextInt(MAX)).limit(50);
    }

    public static Path findResource(String resourceName) throws URISyntaxException {
        Objects.requireNonNull(resourceName);
        var url = Tests.class.getClassLoader().getResource(resourceName);
        var uri = Objects.requireNonNull(url).toURI();
        return Path.of(uri);
    }

}
