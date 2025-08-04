package io.github.sekelenao.smallyaml.test.util;

import java.util.Random;

public final class TestRandomizer {

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

    public static int randomInt(){
        return RANDOM.nextInt(MAX);
    }

}
