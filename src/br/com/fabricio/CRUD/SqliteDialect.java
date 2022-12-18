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

public class SqliteDialect extends Dialect {
    public SqliteDialect() {
        super();
    }

    public void configure(Map<Class<?>, String> dialect) {
       dialect.put(byte.class, "INTEGER");
       dialect.put(short.class, "INTEGER");
       dialect.put(int.class, "INTEGER");
       dialect.put(float.class, "REAL");
       dialect.put(long.class, "REAL");
       dialect.put(double.class, "REAL");
       dialect.put(boolean.class, "BLOB");

       PropertiesManager.ALTER_LIMIT.isAllowed = false;
    }


}
