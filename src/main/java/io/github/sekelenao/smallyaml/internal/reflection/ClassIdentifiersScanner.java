package io.github.sekelenao.smallyaml.internal.reflection;

import io.github.sekelenao.smallyaml.api.document.property.PropertyIdentifier;
import io.github.sekelenao.smallyaml.api.exception.document.DuplicatedIdentifierException;
import io.github.sekelenao.smallyaml.api.exception.document.PropertyDiscoveryException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class ClassIdentifiersScanner {

    private ClassIdentifiersScanner(){
        throw new AssertionError("You cannot instantiate this class");
    }

    private static boolean fieldIsRelevant(Field field) {
        var modifiers = field.getModifiers();
        return Modifier.isStatic(modifiers)
            && Modifier.isPublic(modifiers)
            && Modifier.isFinal(modifiers)
            && PropertyIdentifier.class.isAssignableFrom(field.getType());
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
                            throw DuplicatedIdentifierException.forFollowing(identifier);
                        }
                        set.add(identifier);
                    } catch (IllegalAccessException exception) {
                        throw PropertyDiscoveryException.forFollowing(field, exception);
                    }
                }
            }
            return set;
        }
    };

    public static Set<PropertyIdentifier> scan(Class<?> type){
        Objects.requireNonNull(type);
        return Collections.unmodifiableSet(CACHE.get(type));
    }

}
