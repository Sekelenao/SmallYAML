package io.github.sekelenao.smallyaml.internal.collection;

import java.util.*;

public final class ValueList implements Iterable<String> {

    private String[] values;

    private int nextEmptyIndex = 0;

    public ValueList(String value){
        Objects.requireNonNull(value);
        this.values = new String[8];
        values[nextEmptyIndex++] = value;
    }

    public void add(String value){
        Objects.requireNonNull(value);
        if(nextEmptyIndex == values.length){
            values = Arrays.copyOf(values, values.length * 2);
        }
        values[nextEmptyIndex++] = value;
    }

    public int size(){
        return nextEmptyIndex;
    }

    public String get(int index){
        Objects.checkIndex(index, nextEmptyIndex);
        return values[index];
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<>() {

            private final int version = nextEmptyIndex;

            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                if(version != nextEmptyIndex){
                    throw new ConcurrentModificationException();
                }
                return currentIndex < nextEmptyIndex;
            }

            @Override
            public String next() {
                if(!hasNext()){
                    throw new NoSuchElementException();
                }
                if(version != nextEmptyIndex){
                    throw new ConcurrentModificationException();
                }
                return values[currentIndex++];
            }

        };
    }

}
