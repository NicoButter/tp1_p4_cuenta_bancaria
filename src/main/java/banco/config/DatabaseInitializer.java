/**
 * Sistema Bancario Básico - TP1 Ejercicio 4
 * <p>
 * Clase responsable de inicializar la estructura de la base de datos
 * y cargar datos de prueba.
 * </p>
 * @author Nicolas Butterfield
 * @version 1.1
 * @since Marzo 2025
 */

package banco.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    private final Connection connection;
    private static final String DB_NAME = "banco_db";
    private static final String DB_USER = "banco";
    private static final String DB_PASSWORD = "banco010203";
    
    public DatabaseInitializer(Connection connection) {
        this.connection = connection;
    }

    public void inicializarBDCompleta() {
        try {
            connection.setAutoCommit(false);
            
            crearBaseDeDatosSiNoExiste();
            crearUsuarioSiNoExiste();
            otorgarPrivilegios();
            resetCompleto();
            
            connection.commit();
            System.out.println("Configuración completa de BD realizada con éxito");
        } catch (SQLException e) {
            try {
                connection.rollback();
                System.err.println("Error en configuración BD - Rollback: " + e.getMessage());
            } catch (SQLException ex) {
                System.err.println("Error durante rollback: " + ex.getMessage());
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error restableciendo autocommit: " + e.getMessage());
            }
        }
    }

    private void crearBaseDeDatosSiNoExiste() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            System.out.println("Base de datos verificada/creada: " + DB_NAME);
        }
    }

    private void crearUsuarioSiNoExiste() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(
                "CREATE USER IF NOT EXISTS '" + DB_USER + "'@'localhost' " +
                "IDENTIFIED BY '" + DB_PASSWORD + "'");
            System.out.println("Usuario verificado/creado: " + DB_USER);
        }
    }

    private void otorgarPrivilegios() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(
                "GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON " + DB_NAME + ".* " +
                "TO '" + DB_USER + "'@'localhost'");
            
            stmt.executeUpdate(
                "GRANT LOCK TABLES ON " + DB_NAME + ".* TO '" + DB_USER + "'@'localhost'");
            
            System.out.println("Privilegios otorgados al usuario");
        }
    }

    public void resetCompleto() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("USE " + DB_NAME);
            
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            
            stmt.executeUpdate("DROP TABLE IF EXISTS movimientos");
            stmt.executeUpdate("DROP TABLE IF EXISTS cuentas");
            
            stmt.executeUpdate("CREATE TABLE cuentas ("
                + "id_cuenta INT AUTO_INCREMENT PRIMARY KEY,"
                + "cuenta INT NOT NULL,"
                + "cliente VARCHAR(255) NOT NULL,"
                + "saldo DOUBLE NOT NULL DEFAULT 0,"
                + "tipo_cuenta CHAR(1) NOT NULL"
                + ") ENGINE=InnoDB");
            
            stmt.executeUpdate("CREATE TABLE movimientos ("
                + "id_movimiento INT AUTO_INCREMENT PRIMARY KEY,"
                + "id_cuenta INT NOT NULL,"
                + "mov CHAR(1) NOT NULL,"
                + "importe DOUBLE NOT NULL,"
                + "CONSTRAINT fk_movimientos_cuentas "
                + "FOREIGN KEY (id_cuenta) REFERENCES cuentas(id_cuenta)"
                + ") ENGINE=InnoDB");
            
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
            
            insertarDatosPrueba(stmt);
        }
    }

    private void insertarDatosPrueba(Statement stmt) throws SQLException {
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
            
        System.out.println("Datos de prueba insertados");
    }

    public static String getDbUser() {
        return DB_USER;
    }

    public static String getDbPassword() {
        return DB_PASSWORD;
    }

    public static String getDbName() {
        return DB_NAME;
    }
}


//foyarzo@uarg.unpa.edu.ar