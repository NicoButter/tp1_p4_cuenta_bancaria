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

import banco.model.Cuenta;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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

    public List<Cuenta> listarTodasLasCuentas() throws SQLException {
        String sql = "SELECT id_cuenta, cuenta, cliente, saldo, tipo_cuenta FROM cuentas";
        List<Cuenta> cuentas = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cuentas.add(new Cuenta(
                        rs.getInt("id_cuenta"),
                        rs.getInt("cuenta"),
                        rs.getString("cliente"),
                        rs.getDouble("saldo"),
                        rs.getString("tipo_cuenta").charAt(0)));
            }
        }
        return cuentas;
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

    /**
     * Crea una nueva cuenta bancaria y devuelve su número de cuenta visible
     * 
     * @param cliente      Nombre del titular de la cuenta
     * @param saldoInicial Saldo inicial de la cuenta
     * @param tipoCuenta   Tipo de cuenta ('C'=Corriente, 'A'=Ahorro)
     * @return Número de cuenta visible generado
     * @throws SQLException Si ocurre un error al acceder a la base de datos
     */
    public int crearCuenta(String cliente, double saldoInicial, char tipoCuenta) throws SQLException {
        // Validaciones (se mantienen igual)
        if (cliente == null || cliente.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío");
        }
        if (saldoInicial < 0) {
            throw new IllegalArgumentException("El saldo inicial no puede ser negativo");
        }
        if (tipoCuenta != 'C' && tipoCuenta != 'A') {
            throw new IllegalArgumentException("Tipo de cuenta inválido. Use 'C' para Corriente o 'A' para Ahorro");
        }
    
        String sql = "INSERT INTO cuentas (cuenta, cliente, saldo, tipo_cuenta) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Primero obtenemos el próximo ID estimado
            int nextId = obtenerProximoId();
            int numeroCuenta = 1000 + nextId;
            
            stmt.setInt(1, numeroCuenta);
            stmt.setString(2, cliente);
            stmt.setDouble(3, saldoInicial);
            stmt.setString(4, String.valueOf(tipoCuenta));
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La creación de cuenta falló");
            }
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    System.out.println("Cuenta creada exitosamente: " + numeroCuenta);
                    return numeroCuenta;
                }
            }
            throw new SQLException("No se pudo obtener la cuenta creada");
        }
    }
    
    private int obtenerProximoId() throws SQLException {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW TABLE STATUS LIKE 'cuentas'")) {
            if (rs.next()) {
                return rs.getInt("Auto_increment");
            }
        }
        throw new SQLException("No se pudo obtener el próximo ID");
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

    public int obtenerIdDeCuenta(int numeroCuenta) throws SQLException {
        String sql = "SELECT id_cuenta FROM cuentas WHERE cuenta = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, numeroCuenta);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    public int eliminarCuentaPorId(int idCuenta) throws SQLException {
        String sql = "DELETE FROM cuentas WHERE id_cuenta = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idCuenta);
            return stmt.executeUpdate();
        }
    }
}
