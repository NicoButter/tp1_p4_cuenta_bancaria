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

public class CuentasDAO {
    
    /**
     * Obtiene el saldo actual de una cuenta.
     * @param numeroCuenta Número de cuenta (debe existir)
     * @return Saldo actual como double
     * @throws SQLException Si la cuenta no existe o hay error en la consulta
     */

    private final Connection connection;

    public CuentasDAO(Connection connection) {
        this.connection = connection;
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