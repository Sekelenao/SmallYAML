/**
 * Line provider utilities for reading character data line by line.
 *
 * <p>This package exposes a small pull-based API to iterate over lines coming
 * from common sources such as in-memory text, {@code BufferedReader}, or
 * {@code InputStream} with a chosen {@code Charset}. Implementations do not
 * include trailing line break characters in returned lines.</p>
 *
 * <p>Resources passed to factory methods are not closed automatically unless
 * explicitly documented; callers remain responsible for managing resource
 * lifecycles.</p>
 *
 * @since 0.1.0
 */
package io.github.sekelenao.smallyaml.api.line.provider;