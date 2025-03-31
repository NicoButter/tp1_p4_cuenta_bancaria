/**
 * Sistema Bancario Básico - TP1 Ejercicio 4
 * <p>
 * Clase responsable de inicializar la estructura de la base de datos
 * y cargar datos de prueba.
 * </p>
 */


package banco.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    private final Connection connection;

    public DatabaseInitializer(Connection connection) {
        this.connection = connection;
    }

    public void crearEstructura() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS movimientos");
            stmt.executeUpdate("DROP TABLE IF EXISTS cuentas");

            stmt.executeUpdate("CREATE TABLE cuentas ("
                + "id_cuenta INT AUTO_INCREMENT PRIMARY KEY,"
                + "cuenta INT NOT NULL,"
                + "cliente VARCHAR(255) NOT NULL,"
                + "saldo DOUBLE NOT NULL DEFAULT 0,"
                + "tipo_cuenta CHAR(1) NOT NULL"
                + ")");

            stmt.executeUpdate("CREATE TABLE movimientos ("
                + "id_movimiento INT AUTO_INCREMENT PRIMARY KEY,"
                + "id_cuenta INT NOT NULL,"
                + "mov CHAR(1) NOT NULL,"
                + "importe DOUBLE NOT NULL,"
                + "FOREIGN KEY (id_cuenta) REFERENCES cuentas(id_cuenta)"
                + ")");
        }
    }

    public void insertarDatosPrueba() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("INSERT INTO cuentas (cuenta, cliente, saldo, tipo_cuenta) VALUES "
                + "(1001, 'Nicolas Butterfield', 150000, 'A'),"
                + "(1002, 'María Cavero', 250000, 'C'),"
                + "(1003, 'Luis López', 50000, 'A'),"
                + "(1004, 'Ana Paula Cáseres', 750000, 'C'),"
                + "(1005, 'Leticia Collazo', 300000, 'A'),"
                + "(1006, 'Viggo Butterfield', 125000, 'C')");

            stmt.executeUpdate("INSERT INTO movimientos (id_cuenta, mov, importe) VALUES "
                + "(1, 'D', 150000),"
                + "(2, 'D', 250000),"
                + "(2, 'E', 50000),"
                + "(3, 'D', 50000),"
                + "(4, 'D', 750000),"
                + "(5, 'D', 300000),"
                + "(6, 'D', 125000)");
        }
    }

    public void inicializarBD() {
        try {
            crearEstructura();
            insertarDatosPrueba();
            System.out.println("✅ Base de datos inicializada correctamente");
        } catch (SQLException e) {
            System.err.println("❌ Error al inicializar BD: " + e.getMessage());
        }
    }

     /**
     * Resetea completamente la base de datos (DESTRUCTIVO)
     * @throws SQLException Si ocurre un error durante el reset
     */
    public void resetCompleto() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            
            stmt.executeUpdate("DROP TABLE IF EXISTS movimientos");
            stmt.executeUpdate("DROP TABLE IF EXISTS cuentas");
            
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
            
            crearEstructura();
            insertarDatosPrueba();
        }
    }
}