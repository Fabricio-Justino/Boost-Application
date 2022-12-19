package br.com.fabricio.CRUD;

import br.com.fabricio.anottation.Setter;
import br.com.fabricio.anottation.UsingSetters;
import br.com.fabricio.connection.ConnectionFactory;
import br.com.fabricio.exceptions.NoEntityClass;
import br.com.fabricio.utils.TextFormat;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public enum CRUD {
    INSTANCE;
    private File file;
    private final Set<Class<?>> CREATED_TABLES;

    CRUD() {
        this.CREATED_TABLES = new HashSet<>();
    }
    public void createCrud(String filepath) {
        File file = new File(filepath);
        createFileIfNotExists(file);
        this.file = file;
    }

    public void createTable(Class<?> cls) {
        final String SQL = QueryCreator.createTable(cls);
        Connection connection = ConnectionFactory.get();

        try (Statement statement = connection.createStatement()) {
            statement.execute(SQL);
            this.CREATED_TABLES.add(cls);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(Object entity) {
        final String SQL = QueryCreator.insertInto(entity.getClass(), entity);
        Connection connection = ConnectionFactory.get();

        try (Statement statement = connection.createStatement()) {
            statement.execute(SQL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> Collection<T> findAll(Class<T> table) {
        final Collection<T> RESULT = new ArrayList<>();

        final String ENTITY = QueryCreator.makeTableName(table);
        final List<Field> fields = Arrays.asList(table.getDeclaredFields());

        final String SQL = QueryCreator.findAll(table);
        Connection connection = ConnectionFactory.get();

        try (Statement statement = connection.createStatement()) {
            statement.execute(SQL);
            try (ResultSet resultSet = statement.getResultSet()) {

                while (resultSet.next()) {
                    Constructor<?> constructor = table.getConstructor();
                    Object aux = constructor.newInstance();
                    newInstance(resultSet, aux, fields);
                    RESULT.add((T) aux);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException("Method " + ex.getMessage() + " don't exists", ex.getCause());
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        }
        return RESULT;
    }

    private void newInstance(ResultSet resultSet, Object aux, List<Field> fields) throws SQLException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        for (int i = 0; i < fields.size(); i++) {
            final Field FIELD = fields.get(i);
            final Object VALUE = resultSet.getObject(QueryCreator.makeColumnName(FIELD));
            setValueOf(aux, FIELD, VALUE);
        }
    }

    private void newInstance(ResultSet resultSet, Object aux, Field ...fields) throws SQLException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        newInstance(resultSet, aux, Arrays.asList(fields));
    }

    public <T> T findById(Class<T> table, Object ID) {

        return null;
    }

    private <T> void setValueOf(T entity, Field field, Object value) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (entity.getClass().isAnnotationPresent(UsingSetters.class)) {
            Method method = field.isAnnotationPresent(Setter.class) ?
                    entity.getClass().getDeclaredMethod(field.getAnnotation(Setter.class).name(), field.getType()) :
                    entity.getClass().getDeclaredMethod("set" + TextFormat.title(field.getName()), field.getType());

            method.invoke(entity, value);
        } else {
            field.setAccessible(true);
            field.set(entity, value);
        }
    }

    private void createFileIfNotExists(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
