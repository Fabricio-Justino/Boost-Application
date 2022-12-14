package br.com.fabricio.CRUD;

import br.com.fabricio.anottation.constraint.Id;
import br.com.fabricio.anottation.constraint.VarChar;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SqliteDialect implements Dialect {
    private Map<Class<?>, String> dialect;


    public SqliteDialect() {
        this.dialect = new HashMap<>();
        configure(this.dialect);
    }


    @Override
    public String get(Class<?> cls) {
        return this.dialect.get(cls);
    }

    @Override
    public String get(Class<?> cls, Field field) {
        if (!verifyIfThereIsConstrained(field))
            return this.get(cls);

        return null;
    }

    @Override
    public void configure(Map<Class<?>, String> dialect) {
       dialect.put(byte.class, "TINYINT");
       dialect.put(short.class, "TINYINT");
       dialect.put(int.class, "INT");
       dialect.put(float.class, "REAL");
       dialect.put(long.class, "BIGINT");
       dialect.put(double.class, "REAL");
       dialect.put(boolean.class, "BLOB");

    }

    @Override
    public boolean verifyIfThereIsConstrained(Field field) {

        return false;
    }
}
