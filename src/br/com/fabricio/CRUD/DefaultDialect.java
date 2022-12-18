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

public class DefaultDialect extends Dialect {



    public DefaultDialect() {
        super();
        super.allowAllProperties();
    }


    @Override
    public void configure(Map<Class<?>, String> dialect) {
        dialect.put(byte.class, "TINYINT");
        dialect.put(short.class, "TINYINT");
        dialect.put(int.class, "INT");
        dialect.put(float.class, "REAL");
        dialect.put(long.class, "BIGINT");
        dialect.put(double.class, "FLOAT");
        dialect.put(boolean.class, "BIT");


    }
}
