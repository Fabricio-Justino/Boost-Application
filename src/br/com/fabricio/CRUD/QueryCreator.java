package br.com.fabricio.CRUD;

import br.com.fabricio.anottation.*;
import br.com.fabricio.anottation.constraint.Id;
import br.com.fabricio.anottation.constraint.NotNull;
import br.com.fabricio.anottation.constraint.VarChar;
import br.com.fabricio.exceptions.ManyIdException;
import br.com.fabricio.exceptions.NoEntityClass;
import br.com.fabricio.utils.ReadProperties;
import br.com.fabricio.utils.TextFormat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class QueryCreator {
    private static Dialect dialect = new DefaultDialect();

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

    public static String update(Class<?> table, Object target) {
        String query = null;
        final Field ID = getIdOf(table).orElseThrow(() -> new NullPointerException("The class must have an ID"));
        ID.setAccessible(true);
        try {
            String columns = Arrays.stream(table.getDeclaredFields()).map(field -> {
                field.setAccessible(true);
                try {
                    return "%s = '%s'".formatted(makeColumnName(field), field.get(target));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.joining(", "));
            final String LIMIT = dialect.getPropertyValueIfIsAllowed("ALTER_LIMIT")
                    .orElseGet(() -> "")
                    .transform(str -> {
                        if (str.isBlank()) return "";
                        return "\nLIMIT " + str;
                    });

            query = "UPDATE %s SET %s WHERE %s = '%s'%s".formatted(makeTableName(table), columns, makeColumnName(ID), ID.get(target), LIMIT);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return query;
    }

    public static String findById(Class<?> table, Object target) {
        String query = null;
        final Field ID = getIdOf(table).orElseThrow(() -> new NullPointerException("The class must have an ID"));
        ID.setAccessible(true);
        try {
            query = "SELECT * FROM %s WHERE %s = '%s'".formatted(makeTableName(table), ID.getName(), ID.get(target));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return query;
    }

    public static String insertInto(Class<?> table, Object target) {
        String query = null;
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();

        Arrays.stream(table.getDeclaredFields()).forEach(field -> {
            field.setAccessible(true);
            columns.add(makeColumnName(field));
            try {
                values.add("'%s'".formatted(field.get(target).toString()));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        final String COLUMNS = columns.stream().collect(Collectors.joining(", "));
        final String VALUES = values.stream().collect(Collectors.joining(", "));
        final String IGNORE = dialect.getPropertyNameIfItsValueIsExpected("INSERT_OR_IGNORE", "true")
                .orElseGet(() -> "")
                .transform(str -> TextFormat.splitSkipJoin(str, "_", " ", 1));
        query = "INSERT %s INTO %s (%s) VALUES (%s)".formatted(IGNORE, makeTableName(table), COLUMNS, VALUES);

        return query;
    }

    private static void createHeader(StringBuilder builder, Class<?> cls) {
        String CREATED = dialect.getPropertyNameIfItsValueIsExpected("CREATE_IF_NOT_EXISTS", "true")
                .orElseGet(() -> "")
                        .transform(str -> TextFormat.splitSkipJoin(str, "_", " ", 1));

        final String CREAT_TABLE = "CREATE TABLE %s ".formatted(CREATED);
        builder.append(CREAT_TABLE + makeTableName(cls));
        builder.append(" (\n");
    }

    private static void createBody(StringBuilder build, Field[] fields) {
        final int NUMBER_OF_ID_ALLOWED = 1;
        if (getFielsWithAnnotation(fields, Id.class).size() != NUMBER_OF_ID_ALLOWED)
            throw new ManyIdException();

        Field uniqueId = null;
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            createColumn(build, fields[i], (i == fields.length - 1));
        }
        build.append(")");
    }

    private static void createColumn(StringBuilder builder, Field field, boolean isLastField) {
        builder.append("%s ".formatted(makeColumnName(field)));
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
                builder.append("VARCHAR(%d)".formatted(field.getAnnotation(VarChar.class).value()));
            else
                builder.append("TEXT ");
        }
            dialect.get(field.getType()).ifPresent(str -> builder.append(str + " "));
    }
    
    private static Optional<Field> getIdOf(Class<?> table) {
        return Arrays.stream(table.getDeclaredFields()).filter(f -> f.isAnnotationPresent(Id.class)).findAny();
    }

    private static List<Field> getFielsWithAnnotation(Class<?> table, Class<? extends Annotation> annotation) {
        return Arrays.asList(table.getDeclaredFields()).stream().filter(field -> field.isAnnotationPresent(annotation)).toList();
    }

    private static List<Field> getFielsWithAnnotation(Field fields[], Class<? extends Annotation> annotation) {
        return Arrays.asList(fields).stream().filter(field -> field.isAnnotationPresent(annotation)).toList();
    }

    private static String makeTableName(Class<?> table) {
        if (table.isAnnotationPresent(Table.class))
            return table.getAnnotation(Table.class).name();
        else
            return TextFormat.toSnakeCase(table.getSimpleName());
    }

    private static String makeColumnName(Field field) {
        if (field.isAnnotationPresent(Column.class))
            return field.getAnnotation(Table.class).name();
        else
            return TextFormat.toSnakeCase(field.getName());
    }
}
