/**
 * Document model of the SmallYAML public API.
 *
 * <p>Provides the {@link io.github.sekelenao.smallyaml.api.document.Document}
 * abstraction and a permissive implementation to iterate over key–value
 * properties parsed from YAML content. Keys are unique, and values may be
 * single or multiple, depending on the property.</p>
 *
 * <p>SmallYAML intentionally supports a constrained subset of YAML to keep
 * parsing predictable and the API easy to reason about. The behavior and
 * guarantees described here apply to that subset.</p>
 *
 * @since 1.0.0
 */
package io.github.sekelenao.smallyaml.api.document;