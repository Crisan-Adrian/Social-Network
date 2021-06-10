package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCUtils {

    private final Properties jdbcProps;
    private Connection instance = null;

    public JDBCUtils(Properties props) {
        jdbcProps = props;
    }

    private Connection getNewConnection() {
        String url = jdbcProps.getProperty("database.socialnetwork.url");
        String user = jdbcProps.getProperty("database.socialnetwork.user");
        String pass = jdbcProps.getProperty("database.socialnetwork.password");
        Connection connection = null;
        try {

            if (user != null && pass != null) {
                connection = DriverManager.getConnection(url, user, pass);
            } else {
                connection = DriverManager.getConnection(url);
            }
        } catch (SQLException e) {
            System.out.println("Error getting connection " + e);
        }
        return connection;
    }

    public Connection getConnection() {
        try {
            if (instance == null || instance.isClosed()) {
                instance = getNewConnection();
            }

        } catch (SQLException e) {
            System.out.println("Error DB " + e);
        }
        return instance;
    }
}
