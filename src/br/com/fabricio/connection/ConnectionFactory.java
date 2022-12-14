package br.com.fabricio.connection;

import br.com.fabricio.utils.ReadProperties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private static Connection MAIN_CONNECTIONS[];
    private static int index = 0;

    public static Connection get() {
        if (ConnectionFactory.MAIN_CONNECTIONS == null) {
            ConnectionFactory.init();
        }

        if (ConnectionFactory.index > ConnectionFactory.MAIN_CONNECTIONS.length - 1) {
            index = 0;
            try {
                ConnectionFactory.MAIN_CONNECTIONS[ConnectionFactory.index].close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ConnectionFactory.MAIN_CONNECTIONS[ConnectionFactory.index++];
    }

    private static void init() {
        ConnectionFactory.MAIN_CONNECTIONS = new Connection[10];
        final String url = ReadProperties.INSTANCE.get("URL_CONNECTION");

        try {
            for (int i = 0; i < ConnectionFactory.MAIN_CONNECTIONS.length; i++) {
                ConnectionFactory.MAIN_CONNECTIONS[i] = DriverManager.getConnection(url);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void closeAll() {
        for (int i = 0; i < ConnectionFactory.MAIN_CONNECTIONS.length; i++) {
            try {
                ConnectionFactory.MAIN_CONNECTIONS[i].close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
