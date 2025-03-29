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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}