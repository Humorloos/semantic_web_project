package backend.exception;

public class InvalidUriInputException extends Exception {

  private final String uri;

  public InvalidUriInputException(final String uri) {
    this.uri = uri;
  }
  @Override
  public String getMessage() {
    return String.format("Resource '%s' not found.", uri);
  }
}
