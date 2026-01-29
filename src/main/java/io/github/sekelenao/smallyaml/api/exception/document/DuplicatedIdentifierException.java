package io.github.sekelenao.smallyaml.api.exception.document;

import io.github.sekelenao.smallyaml.api.document.property.PropertyIdentifier;
import io.github.sekelenao.smallyaml.api.exception.SmallYAMLException;

public class DuplicatedIdentifierException extends SmallYAMLException {

  private DuplicatedIdentifierException(String message) {
    super(message);
  }

  public static DuplicatedIdentifierException forFollowing(PropertyIdentifier identifier){
    return new DuplicatedIdentifierException("Duplicated identifier definition: '" + identifier.key() + "'");
  }

}
