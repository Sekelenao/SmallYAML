package io.github.sekelenao.smallyaml.api.document.property;

public interface PropertyIdentifier {

    String key();

    Property.Type type();

    Property.Presence presence();

}
