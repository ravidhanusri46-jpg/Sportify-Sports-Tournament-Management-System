import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/sports_management?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = System.getenv("SPORTIFY_DB_PASSWORD");

    public static Connection getConnection() throws SQLException {
    if (PASSWORD == null) {
        throw new SQLException("Database password is not configured.");
    }

    return DriverManager.getConnection(URL, USER, PASSWORD);
}
}