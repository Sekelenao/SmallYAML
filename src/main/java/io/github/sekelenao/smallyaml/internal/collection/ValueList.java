package io.github.sekelenao.smallyaml.internal.collection;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;

public final class ValueList implements Iterable<String> {

    private String[] values = new String[8];

    private int nextEmptyIndex = 0;

    public ValueList(String value){
        Objects.requireNonNull(value);
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
                return values[currentIndex++];
            }

        };
    }

    public List<String> asListView(){
        return asListView(Function.identity());
    }

    public <T> List<T> asListView(Function<? super String, T> mapper){
        return new AbstractList<>() {

            @Override
            public T get(int index) {
                Objects.checkIndex(index, nextEmptyIndex);
                return mapper.apply(values[index]);
            }

            @Override
            public int size() {
                return nextEmptyIndex;
            }

        };
    }

}
