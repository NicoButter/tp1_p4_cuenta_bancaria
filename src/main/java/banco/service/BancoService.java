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

import banco.dao.CuentasDAO;
import banco.dao.MovimientosDAO;

public class BancoService {
    private final CuentasDAO cuentasDAO;
    private final MovimientosDAO movimientosDAO;
    private final Connection connection;

    public BancoService(Connection connection) {
        this.connection = connection;
        this.cuentasDAO = new CuentasDAO(connection);
        this.movimientosDAO = new MovimientosDAO(connection);
    }

    public void depositar(int numeroCuenta, double monto) throws SQLException {
        validarMontoPositivo(monto);
        
        try {
            connection.setAutoCommit(false);
            
            if (!cuentasDAO.existeCuenta(numeroCuenta)) {
                throw new SQLException("La cuenta no existe");
            }

            cuentasDAO.actualizarSaldo(numeroCuenta, monto, 'd');
            movimientosDAO.registrarMovimiento(numeroCuenta, 'd', monto);
            
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void extraer(int numeroCuenta, double monto) throws SQLException {
        validarMontoPositivo(monto);
        
        try {
            connection.setAutoCommit(false);
            
            if (!cuentasDAO.existeCuenta(numeroCuenta)) {
                throw new SQLException("La cuenta no existe");
            }

            double saldoActual = cuentasDAO.obtenerSaldo(numeroCuenta);
            if (saldoActual < monto) {
                throw new SQLException("Saldo insuficiente");
            }

            cuentasDAO.actualizarSaldo(numeroCuenta, monto, 'e');
            movimientosDAO.registrarMovimiento(numeroCuenta, 'e', monto);
            
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void consultarCuenta(int numeroCuenta) throws SQLException {
        cuentasDAO.consultarDatosCuenta(numeroCuenta);
    }

    private void validarMontoPositivo(double monto) throws SQLException {
        if (monto <= 0) {
            throw new SQLException("El monto debe ser positivo");
        }
    }
}