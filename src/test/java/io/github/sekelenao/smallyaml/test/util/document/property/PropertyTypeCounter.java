package io.github.sekelenao.smallyaml.test.util.document.property;

import io.github.sekelenao.smallyaml.api.document.property.Property;

public final class PropertyTypeCounter {

    private int amountOfSingleValueProperties;

    private int amountOfMultipleValuesProperties;

    public void encountered(Property.Type type) {
        switch (type){
            case SINGLE -> amountOfSingleValueProperties++;
            case MULTIPLE ->  amountOfMultipleValuesProperties++;
        }
    }

    @Override
    public int hashCode() {
        return amountOfSingleValueProperties ^ amountOfMultipleValuesProperties;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof PropertyTypeCounter otherCounter
            && amountOfSingleValueProperties ==  otherCounter.amountOfSingleValueProperties
            && amountOfMultipleValuesProperties == otherCounter.amountOfMultipleValuesProperties;
    }

    @Override
    public String toString() {
        return "PropertyTypeCounter{" +
            "single=" + amountOfSingleValueProperties +
            ", multiple=" + amountOfMultipleValuesProperties +
            '}';
    }

}
