/**
 * Sistema Bancario Básico - TP1 Ejercicio 4
 * <p>
 * Trabajo Práctico N°1 de la materia Laboratorio de Programación (2025) 
 * de la Licenciatura en Sistemas de la Universidad Nacional de la Patagonia Austral (UNPA-UARG).
 * </p>
 * <p>
 * Modelo que representa una cuenta bancaria con sus atributos esenciales.
 * Se mapea directamente a la tabla 'cuentas' en la base de datos.
 * </p>
 * 
 * @author Nicolas Butterfield
 * @version 1.1
 * @since Marzo 2025
 */


package banco.model;

public class Cuenta {
    private int idCuenta;
    private int numeroCuenta;
    private String cliente;
    private double saldo;
    private char tipoCuenta; // 'A' o 'C'

    public Cuenta(int idCuenta, int numeroCuenta, String cliente, double saldo, char tipoCuenta) {
        this.idCuenta = idCuenta;
        this.numeroCuenta = numeroCuenta;
        this.cliente = cliente;
        this.saldo = saldo;
        this.tipoCuenta = tipoCuenta;
    }

    public int getIdCuenta() { return idCuenta; }
    public int getNumeroCuenta() { return numeroCuenta; }
    public String getCliente() { return cliente; }
    public double getSaldo() { return saldo; }
    public char getTipoCuenta() { return tipoCuenta; }

    @Override
    public String toString() {
        return String.format("%8d | %-20s | %-7s | $%,10.2f",
                numeroCuenta,
                cliente,
                tipoCuenta == 'A' ? "Ahorro" : "Corriente",
                saldo);
    }
}