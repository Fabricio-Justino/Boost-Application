package br.com.fabricio.CRUD;

import br.com.fabricio.anottation.*;
import br.com.fabricio.anottation.constraint.Id;
import br.com.fabricio.anottation.constraint.NotNull;
import br.com.fabricio.anottation.constraint.VarChar;
import br.com.fabricio.exceptions.ManyIdException;
import br.com.fabricio.exceptions.NoEntityClass;
import br.com.fabricio.utils.ReadProperties;
import br.com.fabricio.utils.TextFormat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class QueryCreator {
    private static Dialect dialect;

    public static void useDialect(Dialect dialect) {
        QueryCreator.dialect = dialect;
    }

    public static String createTable(Class<?> cls)  {
        if (!cls.isAnnotationPresent(Entity.class)) throw new NoEntityClass();
        
        final Field[] FIELDS = cls.getDeclaredFields();
        final Method[] Methods = cls.getDeclaredMethods();
        final StringBuilder QUERY = new StringBuilder();

        createHeader(QUERY, cls);
        createBody(QUERY, FIELDS);
        return QUERY.toString();
    }

    private static void createHeader(StringBuilder builder, Class<?> cls) {
        String canGet = ReadProperties.INSTANCE.get("CREATE_IF_NOT_EXISTS");

        String rdResult = (canGet != null) ? canGet.equals("true") ? "IF NOT EXISTS" : "" : "";

        final String CREAT_TABLE = "CREATE TABLE %s ".formatted(rdResult);
        builder.append(CREAT_TABLE + createTableName(cls));
        builder.append(" (\n");
    }

    private static String createTableName(Class<?> cls) {
        if (cls.isAnnotationPresent(Table.class))
            return cls.getAnnotation(Table.class).name();
        else
            return TextFormat.toSnakeCase(cls.getSimpleName());
    }

    private static String createColumnName(Field field) {
        if (field.isAnnotationPresent(Column.class))
            return field.getAnnotation(Table.class).name();
        else
            return TextFormat.toSnakeCase(field.getName());
    }

    private static void createBody(StringBuilder build, Field[] fields) {
        Field uniqueId = null;
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            uniqueId = verifyFieldCount(uniqueId, fields[i]);
            createColumn(build, fields[i], (i == fields.length - 1));
        }
        build.append(")");
    }

  

    private static void createColumn(StringBuilder builder, Field field, boolean isLastField) {

        if (field.isAnnotationPresent(Column.class))
            builder.append("%s ".formatted(field.getAnnotation(Column.class).name()));
        else
            builder.append("%s ".formatted(TextFormat.toSnakeCase(field.getName())));

        typeFind(builder, field);
        if (field.isAnnotationPresent(NotNull.class))
            builder.append("NOT NULL ");

        if (field.isAnnotationPresent(Id.class))
            builder.append("UNIQUE PRIMARY KEY");

        builder.append("%s\n".formatted((!isLastField) ? "," : ""));
    }

    private static void typeFind(StringBuilder builder, Field field) {
        if (field.getType().isAssignableFrom(String.class)) {
            if (field.isAnnotationPresent(VarChar.class))
                builder.append("VARCHAR (%d)".formatted(field.getAnnotation(VarChar.class).value()));
            else
                builder.append("TEXT ");
        }

            if (dialect.get(field.getType()) != null)
                builder.append(dialect.get(field.getType()) + " ");
    }
    
    private static Field verifyFieldCount(Field uniqueId, Field field) {
        if (uniqueId != null && field.isAnnotationPresent(Id.class))
            throw new ManyIdException();

        if (field.isAnnotationPresent(Id.class) && uniqueId == null)
            uniqueId = field;

        return uniqueId;
    }
    
    private static Optional<Field> getIdOf(Class<?> table) {
        return Arrays.stream(table.getDeclaredFields()).filter(f -> f.isAnnotationPresent(Id.class)).findAny();
    }
    
    public static String findById(Class<?> table, Object target) {
        String query = null;
        final Field ID = getIdOf(table).orElseThrow(() -> new NullPointerException("The class must have an ID"));
        ID.setAccessible(true);
        try {
            query = "SELECT * FROM %s WHERE %s = '%s'".formatted(createTableName(table), ID.getName(), ID.get(target));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return query;
    }

    public static String insertInto(Class<?> table, Object target) {
        String query = null;
        final Field ID = getIdOf(table).orElseThrow(() -> new NullPointerException("The class must have an ID"));
        ID.setAccessible(true);
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();

        Arrays.stream(table.getDeclaredFields()).forEach(field -> {
            field.setAccessible(true);
            columns.add(createColumnName(field));
            try {
                values.add("'%s'".formatted(field.get(target).toString()));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });


        query = "INSERT INTO %s (%s) VALUES (%s)".formatted(createTableName(table), columns.stream().collect(Collectors.joining(",")), values.stream().collect(Collectors.joining(",")));

        return query;
    }
    public static String update(Class<?> table, Object target) {
        String query = null;
        final Field ID = getIdOf(table).orElseThrow(() -> new NullPointerException("The class must have an ID"));
        ID.setAccessible(true);
        try {
            String columns = Arrays.stream(table.getDeclaredFields()).map(field -> {
                field.setAccessible(true);
                try {
                    return "%s = '%s'".formatted(createColumnName(field), field.get(target));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.joining(", "));
            query = "UPDATE %s SET %s WHERE %s = '%s'".formatted(createTableName(table), columns, createColumnName(ID), ID.get(target));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return query;
    }
}
