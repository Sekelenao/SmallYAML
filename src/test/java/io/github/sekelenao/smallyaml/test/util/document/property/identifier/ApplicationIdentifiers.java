package io.github.sekelenao.smallyaml.test.util.document.property.identifier;

import io.github.sekelenao.smallyaml.api.document.property.Property;
import io.github.sekelenao.smallyaml.api.document.property.PropertyIdentifier;

import java.util.Objects;

public enum ApplicationIdentifiers implements PropertyIdentifier {

        PORT("app.port", Property.Type.SINGLE, Property.Presence.OPTIONAL);
        
        private final String key;
        
        private final Property.Type type;
        
        private final Property.Presence presence;
        
        ApplicationIdentifiers(String key, Property.Type type, Property.Presence presence){
            this.key = Objects.requireNonNull(key);
            this.type = Objects.requireNonNull(type);
            this.presence = Objects.requireNonNull(presence);       
        }

        @Override
        public String key() {
            return key;
        }

        @Override
        public Property.Type type() {
            return type;
        }

        @Override
        public Property.Presence presence() {
            return presence;
        }
        
    }