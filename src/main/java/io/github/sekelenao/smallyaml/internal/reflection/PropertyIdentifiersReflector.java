package io.github.sekelenao.smallyaml.internal.reflection;

import io.github.sekelenao.smallyaml.api.document.property.identifier.PropertyIdentifier;
import io.github.sekelenao.smallyaml.api.document.property.identifier.SmallYAMLPropertyIdentifier;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class PropertyIdentifiersReflector {

    private static boolean fieldIsRelevant(Field field) {
        var modifiers = field.getModifiers();
        return Modifier.isStatic(modifiers)
            && Modifier.isPublic(modifiers)
            && Modifier.isFinal(modifiers)
            && PropertyIdentifier.class.isAssignableFrom(field.getType())
            && field.isAnnotationPresent(SmallYAMLPropertyIdentifier.class);
    }

    private static final ClassValue<Set<PropertyIdentifier>> CACHE = new ClassValue<>() {

        @Override
        protected Set<PropertyIdentifier> computeValue(Class<?> type) {
            Objects.requireNonNull(type);
            var fields = type.getDeclaredFields();
            HashSet<PropertyIdentifier> set = HashSet.newHashSet(fields.length);
            for (var field : fields) {
                if (fieldIsRelevant(field)) {
                    try {
                        var identifier = (PropertyIdentifier) field.get(null);
                        Objects.requireNonNull(identifier);
                        if(set.contains(identifier)){
                            throw new IllegalArgumentException("Duplicated identifier: " + identifier.key());
                        }
                        set.add(identifier);
                    } catch (IllegalAccessException exception) {
                        throw new IllegalStateException("Failed to access to the following field: " + field.getName(), exception);
                    }
                }
            }
            return set;
        }
    };

    public static Set<PropertyIdentifier> get(Class<?> type){
        return Collections.unmodifiableSet(CACHE.get(type));
    }

}
