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
     private static final String DB_NAME = "banco_db";
 
     public DatabaseInitializer(Connection connection) {
         this.connection = connection;
     }
 
     public void inicializarBD() {
         try {
             connection.setAutoCommit(false);
             
             crearBaseDeDatosSiNoExiste();
             resetCompleto();
             
             connection.commit();
             System.out.println("Base de datos inicializada correctamente");
         } catch (SQLException e) {
             try {
                 connection.rollback();
                 System.err.println("Error al inicializar BD - Rollback realizado: " + e.getMessage());
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
             stmt.executeUpdate("USE " + DB_NAME);
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
     }
 }


//foyarzo@uarg.unpa.edu.ar