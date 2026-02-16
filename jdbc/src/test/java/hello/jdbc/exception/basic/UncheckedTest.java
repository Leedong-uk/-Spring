package hello.jdbc.exception.basic;

public class UncheckedTest extends RuntimeException {
  public UncheckedTest(String message) {
    super(message);
  }
}
