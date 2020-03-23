package io.openliberty.guides.application.client;

public class UnknownUriException extends Exception {

  private static final long serialVersionUID = 1L;

  public UnknownUriException() {
    super();
  }

  public UnknownUriException(String message) {
    super(message);
  }
}