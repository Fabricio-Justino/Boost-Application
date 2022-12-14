package br.com.fabricio.exceptions;

public class NoEntityClass extends RuntimeException{
    final static private String DEFAULT_CAUSE = "The class isn't a entity";
    final static private String DEFAULT_MESSAGE = "The class need be annotate with Entity";

    public NoEntityClass() {
        super(DEFAULT_CAUSE, new Throwable(DEFAULT_MESSAGE));
    }

    public NoEntityClass(String msg) {
        super(msg);
    }

    public NoEntityClass(String msg, Throwable cause) {
        super(msg, cause);
    }
}
