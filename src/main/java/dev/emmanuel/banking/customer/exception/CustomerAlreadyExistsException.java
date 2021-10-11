package dev.emmanuel.banking.customer.exception;

import graphql.ErrorClassification;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CustomerAlreadyExistsException extends RuntimeException implements GraphQLError {

  private final String name;

  public CustomerAlreadyExistsException(String message, String name) {
    super(message);
    this.name = name;
  }

  @Override
  public List<SourceLocation> getLocations() {
    return Collections.emptyList();
  }

  @Override
  public ErrorClassification getErrorType() {
    return ErrorType.ValidationError;
  }

  @Override
  public Map<String, Object> getExtensions() {
    return Collections.singletonMap("invalidName", name);
  }
}
