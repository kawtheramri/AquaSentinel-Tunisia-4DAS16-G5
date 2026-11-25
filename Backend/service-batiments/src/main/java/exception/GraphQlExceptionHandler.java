package exception;

public class GraphQlExceptionHandler extends RuntimeException {
    public GraphQlExceptionHandler(String message) {
        super(message);
    }
}
