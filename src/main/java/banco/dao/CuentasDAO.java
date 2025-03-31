/**
 * Sistema Bancario Básico - TP1 Ejercicio 4
 * <p>
 * Trabajo Práctico N°1 de la materia Laboratorio de Programación (2025) 
 * de la Licenciatura en Sistemas de la Universidad Nacional de la Patagonia Austral (UNPA-UARG).
 * </p>
 * 
 * Data Access Object (DAO) para la tabla CUENTAS del sistema bancario.
 * <p>
 * Responsabilidades principales:
 * <ul>
 *   <li>Operaciones CRUD para cuentas bancarias</li>
 *   <li>Validación de existencia de cuentas</li>
 *   <li>Actualización de saldos</li>
 * </ul>
 * </p>
 * 
 * @author Nicolas Butterfield
 * @version 1.0
 * @since Marzo 2025
 * 
 */

package banco.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import banco.config.DatabaseConfig;

public class CuentasDAO {

    private final Connection connection;

    public CuentasDAO(Connection connection) {
        this.connection = connection;
    }

    public String obtenerCliente(int numeroCuenta) throws SQLException {
        String sql = "SELECT cliente FROM cuentas WHERE cuenta = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, numeroCuenta);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("cliente");
            }
            throw new SQLException("Cuenta no encontrada");
        }
    }

    public char obtenerTipoCuenta(int numeroCuenta) throws SQLException {
        String sql = "SELECT tipo_cuenta FROM cuentas WHERE cuenta = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, numeroCuenta);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("tipo_cuenta").charAt(0);
            }
            throw new SQLException("Cuenta no encontrada");
        }
    }

    public int crearCuenta(String cliente, double saldoInicial, char tipoCuenta) throws SQLException {
        String sqlInsert = "INSERT INTO cuentas (cliente, saldo, tipo_cuenta) VALUES (?, ?, ?)";
        String sqlUpdate = "UPDATE cuentas SET cuenta = ? WHERE id_cuenta = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
    
            stmtInsert.setString(1, cliente);
            stmtInsert.setDouble(2, saldoInicial);
            stmtInsert.setString(3, String.valueOf(tipoCuenta));
            stmtInsert.executeUpdate();
    
            int idGenerado;
            try (ResultSet rs = stmtInsert.getGeneratedKeys()) {
                if (!rs.next()) {
                    throw new SQLException("Error al crear cuenta, no se obtuvo ID");
                }
                idGenerado = rs.getInt(1);
            }
    
            int numeroCuentaVisible = 1000 + idGenerado;
            stmtUpdate.setInt(1, numeroCuentaVisible);
            stmtUpdate.setInt(2, idGenerado);
            stmtUpdate.executeUpdate();
    
            return numeroCuentaVisible;
        }
    }

    public boolean eliminarCuenta(int numeroCuenta) throws SQLException {
        String sqlVerificar = "SELECT saldo FROM cuentas WHERE cuenta = ?";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sqlVerificar)) {

            stmt.setInt(1, numeroCuenta);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double saldo = rs.getDouble("saldo");
                if (saldo != 0) {
                    return false;
                }
            } else {
                return false;
            }
        }

        String sqlEliminar = "DELETE FROM cuentas WHERE cuenta = ?";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sqlEliminar)) {

            stmt.setInt(1, numeroCuenta);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean existeCuenta(int numeroCuenta) throws SQLException {
        String sql = "SELECT 1 FROM cuentas WHERE cuenta = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, numeroCuenta);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public double obtenerSaldo(int numeroCuenta) throws SQLException {
        String sql = "SELECT saldo FROM cuentas WHERE cuenta = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, numeroCuenta);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("saldo");
                }
            }
        }
        throw new SQLException("Cuenta no encontrada");
    }

    public void actualizarSaldo(int numeroCuenta, double monto, char tipoOperacion) throws SQLException {
        String operacion = (tipoOperacion == 'd') ? "saldo + ?" : "saldo - ?";
        String sql = "UPDATE cuentas SET saldo = " + operacion + " WHERE cuenta = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, monto);
            stmt.setInt(2, numeroCuenta);
            stmt.executeUpdate();
        }
    }

    public void consultarDatosCuenta(int numeroCuenta) throws SQLException {
        String sql = "SELECT cuenta, cliente, saldo, tipo_cuenta FROM cuentas WHERE cuenta = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, numeroCuenta);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\nDatos de la cuenta:");
                    System.out.println("Número: " + rs.getInt("cuenta"));
                    System.out.println("Cliente: " + rs.getString("cliente"));
                    System.out.println("Tipo: " + (rs.getString("tipo_cuenta").equals("a") ? "Ahorro" : "Corriente"));
                    System.out.println("Saldo actual: $" + rs.getDouble("saldo"));
                } else {
                    System.out.println("Cuenta no encontrada");
                }
            }
        }
    }
}
