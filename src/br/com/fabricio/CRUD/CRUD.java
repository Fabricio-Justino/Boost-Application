package br.com.fabricio.CRUD;

import br.com.fabricio.connection.ConnectionFactory;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public enum CRUD {
    INSTANCE;
    private File file;
    private final List<Class<?>> CREATED_TABLE;

    CRUD() {
        this.CREATED_TABLE = new ArrayList<>();
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
        String sql = QueryCreator.createTable(cls);
        Connection connection = ConnectionFactory.get();

        try (Statement statement = connection.createStatement();) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(Object save) {

    }
}
