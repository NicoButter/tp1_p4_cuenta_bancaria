package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CuentasDAO {
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