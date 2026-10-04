import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Single place to get a JDBC connection.
 * Update URL, USER, PASSWORD to match your local MySQL setup.
 */
public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/bankdb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "darshan0606";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Add mysql-connector-j.jar to classpath.", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}