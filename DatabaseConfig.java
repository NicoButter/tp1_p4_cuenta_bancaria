import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String URL = "jdbc:mariadb://localhost:3306/banco_db";
    private static final String USER = "banco";
    private static final String PASSWORD = "banco010203";

    public static Connection getConnection() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            
            return DriverManager.getConnection(
                URL, 
                USER, 
                PASSWORD
            );
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No encontré el driver JDBC de MariaDB :|");
            System.err.println("¿Incluiste el conector JDBC de MariaDB en tus dependencias? revisalo...");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error de conexión a la base de datos:");
            System.err.println("URL: " + URL);
            System.err.println("Usuario: " + USER);
            e.printStackTrace();
        }
        return null;
    }
}

