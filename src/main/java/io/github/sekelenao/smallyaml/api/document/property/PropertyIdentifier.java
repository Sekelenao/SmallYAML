package io.github.sekelenao.smallyaml.api.document.property;


/**
 * Represents an identifier for a property in a {@link io.github.sekelenao.smallyaml.api.document.BoundedDocument}.
 * An identifier specifies the key, the cardinality (SINGLE or MULTIPLE), and the presence (MANDATORY or OPTIONAL)
 * of a property.
 *
 * @since 0.2.0
 */
public sealed interface PropertyIdentifier permits
    MultipleMandatoryIdentifier, MultipleOptionalIdentifier,
    SingleMandatoryIdentifier, SingleOptionalIdentifier {

    /**
     * Retrieves the key associated with the property identifier.
     *
     * @return the key of the property
     *
     * @since 0.2.0
     */
    String key();

    /**
     * Retrieves the type of the property identifier, which indicates whether the property
     * is associated with a single value or multiple values.
     *
     * @return a {@code Property.Type} representing the cardinality of the property;
     *         either {@code SINGLE} or {@code MULTIPLE}.
     *
     * @since 0.2.0
     */
    Property.Type type();

    /**
     * Retrieves the presence of the property identifier, which indicates whether the property
     * is mandatory or optional.
     *
     * @return a {@code Property.Presence} representing the presence of the property;
     *         either {@code MANDATORY} or {@code OPTIONAL}.
     *
     * @since 0.2.0
     */
    Property.Presence presence();

}
