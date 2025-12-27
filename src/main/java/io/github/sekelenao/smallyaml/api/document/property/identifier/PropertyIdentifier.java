package io.github.sekelenao.smallyaml.api.document.property.identifier;


import io.github.sekelenao.smallyaml.api.document.property.Property;

public sealed interface PropertyIdentifier permits
    MultipleMandatoryDoubleIdentifier, MultipleMandatoryIdentifier,
    MultipleMandatoryIntIdentifier, MultipleMandatoryLongIdentifier,
    MultipleOptionalDoubleIdentifier, MultipleOptionalIdentifier,
    MultipleOptionalIntIdentifier, MultipleOptionalLongIdentifier,
    SingleMandatoryBooleanIdentifier, SingleMandatoryDoubleIdentifier,
    SingleMandatoryIdentifier, SingleMandatoryIntIdentifier,
    SingleMandatoryLongIdentifier, SingleOptionalBooleanIdentifier,
    SingleOptionalDoubleIdentifier, SingleOptionalIdentifier,
    SingleOptionalIntIdentifier, SingleOptionalLongIdentifier {

    /**
     * Retrieves the key associated with the property identifier.
     *
     * @return the key of the property
     */
    String key();

    Property.Type type();

    Property.Presence presence();

}
