package io.github.sekelenao.smallyaml.internal.collection;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

            private Spliterator<T> customSpliterator(int start, int end, String[] array) {
                return new Spliterator<>() {

                    private int index = start;

                    @Override
                    public boolean tryAdvance(Consumer<? super T> action) {
                        if (index < end) {
                            action.accept(mapper.apply(array[index++]));
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public Spliterator<T> trySplit() {
                        if(end - start < 1024){
                            return null;
                        }
                        var middle = (index + end) >>> 1;
                        if (middle == index) {
                            return null;
                        }
                        var spliterator = customSpliterator(index, middle, array);
                        index = middle;
                        return spliterator;
                    }

                    @Override
                    public long estimateSize() {
                        return (end - index);
                    }

                    @Override
                    public int characteristics() {
                        return SIZED | SUBSIZED | ORDERED | NONNULL;
                    }

                };

            }

            @Override
            public Spliterator<T> spliterator() {
                return customSpliterator(0, nextEmptyIndex, values);
            }

            @Override
            public Stream<T> stream() {
                return StreamSupport.stream(spliterator(), false);
            }

        };
    }

    public boolean[] asArrayOfStrictBooleans(){
        var array = new boolean[nextEmptyIndex];
        for(int i = 0; i < nextEmptyIndex; i++){
            var value = values[i];
            if(value.equalsIgnoreCase("TRUE")){
                array[i] = true;
            }
            else if(value.equalsIgnoreCase("FALSE")){
                array[i] = false;
            }
            else {
                throw new IllegalArgumentException("Boolean should be case insensitive 'TRUE' or 'FALSE'");
            }
        }
        return array;
    }

    public int[] asArrayOfInts(){
        var array = new int[nextEmptyIndex];
        for(int i = 0; i < nextEmptyIndex; i++){
            array[i] = Integer.parseInt(values[i]);
        }
        return array;
    }

    public long[] asArrayOfLongs(){
        var array = new long[nextEmptyIndex];
        for(int i = 0; i < nextEmptyIndex; i++){
            array[i] = Long.parseLong(values[i]);
        }
        return array;
    }

    public double[] asArrayOfDoubles(){
        var array = new double[nextEmptyIndex];
        for(int i = 0; i < nextEmptyIndex; i++){
            array[i] = Double.parseDouble(values[i]);
        }
        return array;
    }

}
