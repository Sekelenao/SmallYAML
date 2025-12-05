/**
 * SmallYAML public API root package.
 *
 * <p>This API focuses on clarity and predictability rather than covering the
 * full YAML 1.2 specification. It intentionally supports a constrained subset
 * of YAML so that configuration remains simple and parsing behavior is
 * straightforward and easy to reason about.</p>
 *
 * <p>The subpackages expose:
 * <ul>
 *   <li><code>document</code> — document and property model</li>
 *   <li><code>line.provider</code> — pull-based line readers</li>
 *   <li><code>mapping</code> — helpers for mapping property values</li>
 *   <li><code>exception</code> — checked/runtime exceptions from parsing and access</li>
 * </ul>
 * These packages form the stable, public API surface intended for application use.</p>
 *
 * <p>Semantic versioning applies: breaking changes are only introduced in major
 * versions.</p>
 *
 * @since 0.1.0
 */
package io.github.sekelenao.smallyaml.api;