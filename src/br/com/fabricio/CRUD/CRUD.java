package br.com.fabricio.CRUD;

import br.com.fabricio.connection.ConnectionFactory;
import br.com.fabricio.exceptions.NoEntityClass;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
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

    private void createFileIfNotExists(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void createTable(Class<?> cls) {
        final String SQL = QueryCreator.createTable(cls);
        Connection connection = ConnectionFactory.get();

        try (Statement statement = connection.createStatement();) {
            statement.execute(SQL);
            this.CREATED_TABLES.add(cls);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(Object entity) {
        verifyIfEntityWasCreated(entity);

        final String SQL = QueryCreator.insertInto(entity.getClass(), entity);
        Connection connection = ConnectionFactory.get();

        try (Statement statement = connection.createStatement()) {
            statement.execute(SQL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<Object> findAll(Object entity) {
        verifyIfEntityWasCreated(entity);
    }

    private void verifyIfEntityWasCreated(Object entity) {
        final String EXCEPTION = "There isn't table %s".formatted(entity.getClass().getSimpleName());
        if (!this.CREATED_TABLES.contains(entity.getClass())) throw new NoEntityClass(EXCEPTION);
    }
}
