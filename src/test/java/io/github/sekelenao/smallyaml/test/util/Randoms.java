package io.github.sekelenao.smallyaml.test.util;

import java.util.Random;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
public final class Randoms {

    private static final int MAX_INT_VALUE = 10_000;

    private static final Random RANDOM = new Random();

    private Randoms(){
        throw new AssertionError("You cannot instantiate this class");
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
