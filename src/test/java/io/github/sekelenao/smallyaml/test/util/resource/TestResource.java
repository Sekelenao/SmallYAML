package io.github.sekelenao.smallyaml.test.util.resource;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Objects;

public interface TestResource {

    static Path find(String resourcePath) throws URISyntaxException {
        Objects.requireNonNull(resourcePath);
        var url = TestResource.class.getClassLoader().getResource(resourcePath);
        if(url == null){
            throw new IllegalArgumentException("Resource not found: " + resourcePath);
        }
        return Path.of(url.toURI());
    }

    static Path find(TestResource testResource) throws URISyntaxException {
        Objects.requireNonNull(testResource);
        return find(testResource.resourcePath());
    }

    static InputStream asInputStream(TestResource testResource) {
        return TestResource.class.getClassLoader().getResourceAsStream(testResource.resourcePath());
    }

    String resourcePath();

}
