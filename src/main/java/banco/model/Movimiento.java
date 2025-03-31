package banco.model;

import java.sql.Timestamp;

public class Movimiento {
    private int idMovimiento;
    private int numeroCuenta;
    private char tipo; 
    private double monto;
    private Timestamp fecha;

    public Movimiento(int idMovimiento, int numeroCuenta, char tipo, double monto, Timestamp fecha) {
        this.idMovimiento = idMovimiento;
        this.numeroCuenta = numeroCuenta;
        this.tipo = tipo;
        this.monto = monto;
        this.fecha = fecha;
    }

    // Getters
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
