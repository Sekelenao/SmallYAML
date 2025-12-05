/**
 * Property model used by the SmallYAML document API.
 *
 * <p>Defines the {@code Property} abstraction and concrete shapes for values
 * associated with a property key. A property holds either a single value or a
 * collection of values, represented by dedicated implementations.</p>
 *
 * <p>SmallYAML favors a predictable subset of YAML. As a result, the property
 * model is intentionally simple: keys are unique, and values are exposed as
 * strings that client code can further map to domain types as needed.</p>
 *
 * @since 0.1.0
 */
package io.github.sekelenao.smallyaml.api.document.property;