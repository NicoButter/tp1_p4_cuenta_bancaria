/**
 * Sistema Bancario Básico - TP1 Ejercicio 4
 * <p>
 * Trabajo Práctico N°1 de la materia Laboratorio de Programación (2025) 
 * de la Licenciatura en Sistemas de la Universidad Nacional de la Patagonia Austral (UNPA-UARG).
 * </p>
 * <p>
 * Modelo que representa un movimiento bancario (depósito/extracción).
 * Se mapea directamente a la tabla 'movimientos' en la base de datos.
 * </p>
 * 
 * @author Nicolas Butterfield
 * @version 1.1
 * @since Marzo 2025
 */

package banco.model;

import java.sql.Timestamp;

public class Movimiento {
    private int idMovimiento;
    private int numeroCuenta;
    private char tipo; 
    private double monto;
    private Timestamp fecha;

    public Movimiento(int idMovimiento, int numeroCuenta, char tipo, double monto) {
        this.idMovimiento = idMovimiento;
        this.numeroCuenta = numeroCuenta;
        this.tipo = tipo;
        this.monto = monto;
    }

    public int getIdMovimiento() { return idMovimiento; }
    public int getNumeroCuenta() { return numeroCuenta; }
    public char getTipo() { return tipo; }
    public double getMonto() { return monto; }
    public Timestamp getFecha() { return fecha; }

    @Override
    public String toString() {
        return String.format("%6d | %8d | %-9s | %10.2f",
                idMovimiento,
                numeroCuenta,
                tipo == 'D' ? "DEPÓSITO" : "EXTRACCIÓN",
                monto);
    }
}
