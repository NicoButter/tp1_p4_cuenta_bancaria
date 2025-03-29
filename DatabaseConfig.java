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
            
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.out.println("No encuentro el driver :| revisá la configuración de tu proyecto.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error al intentar conectarse a la base de datos");
            e.printStackTrace();
        }
        return null;
    }
}
