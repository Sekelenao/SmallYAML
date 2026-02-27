package io.github.sekelenao.smallyaml.api.document.property;

import java.util.Locale;
import java.util.Objects;

/**
 * Implementation of {@link PropertyIdentifier} for a single mandatory property.
 *
 * @since 0.2.0
 */
public final class SingleMandatoryIdentifier implements PropertyIdentifier {

    private final String key;

    private SingleMandatoryIdentifier(String key) {
        this.key = key.toLowerCase(Locale.ROOT);
    }

    /**
     * Defines a new single mandatory identifier with the given key.
     *
     * @param key the property key
     * @return a new SingleMandatoryIdentifier instance
     * @throws NullPointerException if the key is null
     *
     * @since 0.2.0
     */
    public static SingleMandatoryIdentifier define(String key) {
        Objects.requireNonNull(key);
        return new SingleMandatoryIdentifier(key);
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public Property.Type type() {
        return Property.Type.SINGLE;
    }

    @Override
    public Property.Presence presence() {
        return Property.Presence.MANDATORY;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    /**
     * Compares this identifier with another object for equality.
     * Two identifiers are considered equal if they have the same key,
     * regardless of their specific implementation class.
     *
     * @param other the object to compare with
     * @return {@code true} if the other object is a {@link PropertyIdentifier}
     *         with the same key, {@code false} otherwise
     *
     * @since 0.2.0
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof PropertyIdentifier otherIdentifier && key.equals(otherIdentifier.key());
    }
}