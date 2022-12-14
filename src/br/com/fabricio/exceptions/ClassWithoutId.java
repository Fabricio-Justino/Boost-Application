package br.com.fabricio.exceptions;

public class ClassWithoutId extends  RuntimeException{
    final static private String DEFAULT_CAUSE = "The class don't have a field ID";
    final static private String DEFAULT_MESSAGE = "The class need an field with annotation Id";

    public ClassWithoutId() {
        super(DEFAULT_MESSAGE, new Throwable(DEFAULT_CAUSE));
    }

    public ClassWithoutId(String msg) {
        super(msg);
    }

    public ClassWithoutId(String msg, Throwable cause) {
        super(msg, cause);
    }
}
