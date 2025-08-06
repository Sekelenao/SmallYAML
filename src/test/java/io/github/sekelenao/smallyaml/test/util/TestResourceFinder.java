package io.github.sekelenao.smallyaml.test.util;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Objects;

public final class TestResourceFinder {

    public static Path findResource(String resourceName) throws URISyntaxException {
        var url = TestResourceFinder.class.getClassLoader().getResource(resourceName);
        var uri = Objects.requireNonNull(url).toURI();
        return Path.of(uri);
    }

}
