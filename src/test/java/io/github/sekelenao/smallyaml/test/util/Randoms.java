package io.github.sekelenao.smallyaml.test.util;

import java.util.Random;
import java.util.stream.IntStream;

public final class Randoms {

    private static final int MAX_INT_VALUE = 10_000;

    private static final String[] BLANK_CHARS = {" ", "\t"};

    private static final Random RANDOM = new Random();

    private Randoms(){
        throw new AssertionError("You cannot instantiate this class");
    }

    public static String blankString(int size){
        var builder = new StringBuilder();
        for(int i = 0; i < size; i++){
            builder.append(BLANK_CHARS[RANDOM.nextInt(BLANK_CHARS.length)]);
        }
        return builder.toString();
    }

    @SuppressWarnings("unused")
    public static IntStream intStreamWithSize5() {
        return IntStream.generate(() -> RANDOM.nextInt(MAX_INT_VALUE)).limit(5);
    }

    @SuppressWarnings("unused")
    public static IntStream intStreamWithSize50() {
        return IntStream.generate(() -> RANDOM.nextInt(MAX_INT_VALUE)).limit(50);
    }

}
