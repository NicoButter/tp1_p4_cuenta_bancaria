/**
 * Sistema Bancario Básico - TP1 Ejercicio 4
 * <p>
 * Trabajo Práctico N°1 de la materia Laboratorio de Programación (2025) 
 * de la Licenciatura en Sistemas de la Universidad Nacional de la Patagonia Austral (UNPA-UARG).
 * </p>
 * 
 * <p>
 * Implementa:
 *  - Gestión transaccional (depósitos/extracciones)
 *  - Validación de reglas de negocio
 *  - Integración con DAOs
 * </p>
 * 
 * @author Nicolas Butterfield
 * @version 1.0
 * @since Marzo 2025
 */

package banco.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import banco.dao.CuentasDAO;
import banco.dao.MovimientosDAO;
import banco.model.Movimiento;

public class BancoService {
    private final CuentasDAO cuentasDAO;
    private final MovimientosDAO movimientosDAO;
    private final Connection connection;

    public BancoService(Connection connection) {
        this.connection = connection;
        this.cuentasDAO = new CuentasDAO(connection);
        this.movimientosDAO = new MovimientosDAO(connection);
    }

    public int crearCuenta(String cliente, double saldoInicial, char tipoCuenta)
            throws SQLException, IllegalArgumentException {

        if (saldoInicial < 0) {
            throw new IllegalArgumentException("El saldo inicial no puede ser negativo");
        }
        if (tipoCuenta != 'A' && tipoCuenta != 'C') {
            throw new IllegalArgumentException("Tipo de cuenta inválido. Use 'A' (Ahorro) o 'C' (Corriente)");
        }

        return cuentasDAO.crearCuenta(cliente, saldoInicial, tipoCuenta);
    }

    // public boolean eliminarCuenta(int numeroCuenta) throws SQLException {
    //     return cuentasDAO.eliminarCuenta(numeroCuenta);
    // }
    
    public boolean eliminarCuenta(int numeroCuenta) throws SQLException {
        try {
            connection.setAutoCommit(false);
            
            // 1. Verificar si la cuenta existe y obtener su id_cuenta
            int idCuenta = cuentasDAO.obtenerIdCuenta(numeroCuenta);
            if (idCuenta == -1) {
                throw new SQLException("La cuenta " + numeroCuenta + " no existe");
            }
            
            // 2. Verificar saldo cero
            double saldo = cuentasDAO.obtenerSaldo(numeroCuenta);
            if (saldo != 0) {
                throw new SQLException("No se puede eliminar: la cuenta tiene saldo $" + saldo);
            }
            
            // 3. Eliminar movimientos asociados primero
            movimientosDAO.eliminarMovimientosPorCuenta(idCuenta);
            
            // 4. Eliminar la cuenta
            boolean eliminada = cuentasDAO.eliminarCuenta(idCuenta);
            
            if (eliminada) {
                connection.commit();
                return true;
            } else {
                connection.rollback();
                return false;
            }
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
    
    public boolean depositar(int numeroCuenta, double monto) throws SQLException {
        validarMontoPositivo(monto); // Valida monto > 0

        try {
            connection.setAutoCommit(false);

            if (!cuentasDAO.existeCuenta(numeroCuenta)) {
                throw new SQLException("La cuenta " + numeroCuenta + " no existe");
            }

            cuentasDAO.actualizarSaldo(numeroCuenta, monto, 'd');
            movimientosDAO.registrarMovimiento(numeroCuenta, 'd', monto);

            connection.commit();
            return true;
        } catch (SQLException e) {
            connection.rollback();
            throw new SQLException("Error en depósito: " + e.getMessage(), e); // Mejor mensaje
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public boolean extraer(int numeroCuenta, double monto) throws SQLException {
        validarMontoPositivo(monto);

        try {
            connection.setAutoCommit(false);

            if (!cuentasDAO.existeCuenta(numeroCuenta)) {
                throw new SQLException("La cuenta " + numeroCuenta + " no existe");
            }

            double saldoActual = cuentasDAO.obtenerSaldo(numeroCuenta);
            if (saldoActual < monto) {
                throw new SQLException("Saldo insuficiente. Disponible: " + saldoActual);
            }

            cuentasDAO.actualizarSaldo(numeroCuenta, monto, 'e');
            movimientosDAO.registrarMovimiento(numeroCuenta, 'e', monto);

            connection.commit();
            return true;
        } catch (SQLException e) {
            connection.rollback();
            throw new SQLException("Error en extracción: " + e.getMessage(), e);
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void consultarCuenta(int numeroCuenta) throws SQLException {
        if (!cuentasDAO.existeCuenta(numeroCuenta)) {
            throw new SQLException("La cuenta " + numeroCuenta + " no existe");
        }
        
        double saldo = cuentasDAO.obtenerSaldo(numeroCuenta);
        String cliente = cuentasDAO.obtenerCliente(numeroCuenta);
        char tipoCuenta = cuentasDAO.obtenerTipoCuenta(numeroCuenta);
        
        System.out.println("\n--- DATOS DE LA CUENTA ---");
        System.out.println("Número: " + numeroCuenta);
        System.out.println("Cliente: " + cliente);
        System.out.println("Tipo: " + (tipoCuenta == 'A' ? "Ahorro" : "Corriente"));
        System.out.printf("Saldo actual: $%.2f\n", saldo);
    }

    private void validarMontoPositivo(double monto) throws SQLException {
        if (monto <= 0) {
            throw new SQLException("El monto debe ser positivo");
        }
    }

    public void mostrarMovimientosCuenta(int numeroCuenta) throws SQLException {
        List<Movimiento> movimientos = movimientosDAO.obtenerMovimientosCuenta(numeroCuenta);

        System.out.println("\n=== MOVIMIENTOS CUENTA " + numeroCuenta + " ===");
        System.out.println("ID     | CUENTA   | TIPO      | IMPORTE");
        System.out.println("---------------------------------------");

        if (movimientos.isEmpty()) {
            System.out.println("No se encontraron movimientos");
        } else {
            movimientos.forEach(System.out::println);
        }
    }

    public void mostrarMovimientosCliente(String nombreCliente) throws SQLException {
        List<Movimiento> movimientos = movimientosDAO.obtenerMovimientosCliente(nombreCliente);
        
        System.out.println("\n=== MOVIMIENTOS DEL CLIENTE: " + nombreCliente.toUpperCase() + " ===");
        System.out.println("ID     | CUENTA   | TIPO      | IMPORTE");
        System.out.println("---------------------------------------");
        
        if (movimientos.isEmpty()) {
            System.out.println("No se encontraron movimientos para este cliente");
        } else {
            movimientos.forEach(mov -> {
                System.out.printf("%6d | %8d | %-9s | %10.2f\n",
                    mov.getIdMovimiento(),
                    mov.getNumeroCuenta(),
                    mov.getTipo() == 'D' ? "DEPÓSITO" : "EXTRACCIÓN",
                    mov.getMonto());
            });
        }
    }

    public void filtrarMovimientosPorTipo(int numeroCuenta, char tipo) throws SQLException {
        if (tipo != 'D' && tipo != 'E') {
            throw new IllegalArgumentException("Tipo debe ser D (Depósito) o E (Extracción)");
        }
        
        List<Movimiento> movimientos = movimientosDAO.obtenerMovimientosPorTipo(numeroCuenta, tipo);
        
        System.out.println("\n=== " + (tipo == 'D' ? "DEPÓSITOS" : "EXTRACCIONES") + 
                          " CUENTA " + numeroCuenta + " ===");
        System.out.println("ID     | CUENTA   | IMPORTE");
        System.out.println("---------------------------");
        
        movimientos.forEach(mov -> {
            System.out.printf("%6d | %8d | %10.2f\n",
                mov.getIdMovimiento(),
                mov.getNumeroCuenta(),
                mov.getMonto());
        });
    }

}
