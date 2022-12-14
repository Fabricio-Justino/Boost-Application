package br.com.fabricio.CRUD;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.List;
import java.util.function.Function;

public interface Dialect {
    String get(Class<?> cls);
    String get(Class<?> cls, Field field);

    void configure(Map<Class<?>, String> dialect);

    boolean verifyIfThereIsConstrained(Field field);
}
