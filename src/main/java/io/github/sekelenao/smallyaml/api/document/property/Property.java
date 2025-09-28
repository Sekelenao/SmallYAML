package io.github.sekelenao.smallyaml.api.document.property;

public sealed interface Property permits SingleValueProperty, MultipleValuesProperty {

    enum Type { SINGLE, MULTIPLE }

}
