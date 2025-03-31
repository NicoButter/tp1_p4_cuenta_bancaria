/**
 * Sistema Bancario Básico - TP1 Ejercicio 4
 * <p>
 * Trabajo Práctico N°1 de la materia Laboratorio de Programación (2025) 
 * de la Licenciatura en Sistemas de la Universidad Nacional de la Patagonia Austral (UNPA-UARG).
 * </p>
 * 
 * Data Access Object (DAO) para la tabla MOVIMIENTOS del sistema bancario.
 <p>
 * Patrones aplicados:
 * <ul>
 *   <li>Data Access Object</li>
 *   <li>Inyección de dependencias</li>
 * </ul>
 * </p>
 * 
 * 
 * @author Nicolas Butterfield
 * @version 1.0
 * @since Marzo 2025
 * 
 */

package banco.dao;

import banco.model.Movimiento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MovimientosDAO {
    private final Connection connection;

    public MovimientosDAO(Connection connection) {
        this.connection = connection;
    }

    public int obtenerIdCuenta(int numeroCuenta) throws SQLException {
        String sql = "SELECT id_cuenta FROM cuentas WHERE cuenta = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, numeroCuenta);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_cuenta");
                }
            }
        }
        throw new SQLException("Cuenta no encontrada");
    }

    public void registrarMovimiento(int numeroCuenta, char tipo, double monto) throws SQLException {
        int idCuenta = obtenerIdCuenta(numeroCuenta);
        String sql = "INSERT INTO movimientos (id_cuenta, mov, importe) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idCuenta);
            stmt.setString(2, String.valueOf(tipo));
            stmt.setDouble(3, monto);
            stmt.executeUpdate();
        }
    }

    public List<Movimiento> obtenerMovimientosCuenta(int numeroCuenta) throws SQLException {
        String sql = "SELECT m.*, c.cuenta as num_cuenta FROM movimientos m " +
                    "JOIN cuentas c ON m.id_cuenta = c.id_cuenta " +
                    "WHERE c.cuenta = ? ORDER BY m.id_movimiento DESC";
        List<Movimiento> movimientos = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, numeroCuenta);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                movimientos.add(new Movimiento(
                    rs.getInt("id_movimiento"),
                    rs.getInt("num_cuenta"), // Usamos el número de cuenta visible
                    rs.getString("mov").charAt(0),
                    rs.getDouble("importe"),
                    null // No hay campo fecha en tu tabla
                ));
            }
        }
        return movimientos;
    }

    public List<Movimiento> obtenerMovimientosPorTipo(int numeroCuenta, char tipo) throws SQLException {
        String sql = "SELECT m.*, c.cuenta as num_cuenta FROM movimientos m " +
                    "JOIN cuentas c ON m.id_cuenta = c.id_cuenta " +
                    "WHERE c.cuenta = ? AND m.mov = ? ORDER BY m.id_movimiento DESC";
        List<Movimiento> movimientos = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, numeroCuenta);
            stmt.setString(2, String.valueOf(tipo));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                movimientos.add(new Movimiento(
                    rs.getInt("id_movimiento"),
                    rs.getInt("cuenta"),
                    rs.getString("tipo").charAt(0), // Corrección aquí
                    rs.getDouble("monto"),
                    rs.getTimestamp("fecha")
                ));
            }
        }
        return movimientos;
    }

    public List<Movimiento> obtenerMovimientosCliente(String nombreCliente) throws SQLException {
        String sql = "SELECT m.*, c.cuenta as num_cuenta FROM movimientos m " +
                    "JOIN cuentas c ON m.id_cuenta = c.id_cuenta " +
                    "WHERE c.cliente LIKE ? ORDER BY m.id_movimiento DESC";
        List<Movimiento> movimientos = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + nombreCliente + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                movimientos.add(new Movimiento(
                    rs.getInt("id_movimiento"),
                    rs.getInt("cuenta"),
                    rs.getString("tipo").charAt(0), // Corrección aquí
                    rs.getDouble("monto"),
                    rs.getTimestamp("fecha")
                ));
            }
        }
        return movimientos;
    }

    public void eliminarMovimientosPorCuenta(int idCuenta) throws SQLException {
        String sql = "DELETE FROM movimientos WHERE id_cuenta = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idCuenta);
            stmt.executeUpdate();
        }
    }
}
