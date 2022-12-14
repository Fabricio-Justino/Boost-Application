package br.com.fabricio.exceptions;

public class ManyIdException extends  RuntimeException {

    final static private String DEFAULT_CAUSE = "The class must have only one Id";
    final static private String DEFAULT_MESSAGE = "Class have more than one Id on its fields";

    public ManyIdException() {
        super(DEFAULT_MESSAGE, new Throwable(DEFAULT_CAUSE));
    }

    public ManyIdException(String msg) {
        super(msg);
    }

    public ManyIdException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
