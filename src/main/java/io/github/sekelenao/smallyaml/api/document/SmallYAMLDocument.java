package io.github.sekelenao.smallyaml.api.document;

import io.github.sekelenao.smallyaml.api.document.property.Property;

import java.util.Iterator;

public interface SmallYAMLDocument {

    Iterator<Property> iterator();

}
