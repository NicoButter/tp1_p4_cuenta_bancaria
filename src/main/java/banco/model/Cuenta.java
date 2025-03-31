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