package io.github.sekelenao.smallyaml.internal.parsing.collector;

import io.github.sekelenao.smallyaml.api.exception.document.DuplicatedPropertyException;
import io.github.sekelenao.smallyaml.internal.collection.ValueList;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class MapParsingCollector implements ParsingCollector {

        private final Map<String, Object> map;

        public MapParsingCollector() {
            this.map = new HashMap<>();
        }

        @Override
        public void collectSingleValue(String key, String value) {
            Objects.requireNonNull(key);
            Objects.requireNonNull(value);
            if(map.containsKey(key)){
                throw DuplicatedPropertyException.forFollowing(key);
            }
            map.put(key, value);
        }

        @Override
        public void collectListValue(String key, String value, boolean isNewList) {
            Objects.requireNonNull(key);
            Objects.requireNonNull(value);
            if(!isNewList && !map.containsKey(key)){
                throw new IllegalStateException("Expected existing list for: " + key);
            }
            map.merge(key, new ValueList(value), (existing, newValue) -> {
                if(isNewList){
                    throw DuplicatedPropertyException.forFollowing(key);
                }
                if (existing instanceof ValueList existingList) {
                    existingList.add(value);
                    return existingList;
                }
                throw new IllegalStateException("Unexpected type: " + existing.getClass());
            });
        }

        public Map<String, Object> underlyingMapAsView(){
            return Collections.unmodifiableMap(map);
        }

    }