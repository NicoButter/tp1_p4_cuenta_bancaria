package banco;

import banco.service.BancoService;
import banco.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection == null) {
                System.err.println("No se pudo establecer conexión con la base de datos");
                return;
            }

            BancoService bancoService = new BancoService(connection);

            while (true) {
                mostrarMenu();
                int opcion = scanner.nextInt();
                scanner.nextLine(); 

                switch (opcion) { 
                    case 1 -> realizarDeposito(bancoService);
                    case 2 -> realizarExtraccion(bancoService);
                    case 3 -> consultarSaldo(bancoService);
                    case 4 -> {
                        System.out.println("Saliendo del sistema...");
                        return;
                    }
                    default -> System.out.println("Opción no válida");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error de conexión a la base de datos:");
            e.printStackTrace();
        }
    }

    private static void mostrarMenu() {
        System.out.println("\n==== SISTEMA BANCARIO ====");
        System.out.println("1. Depositar");
        System.out.println("2. Extraer");
        System.out.println("3. Consultar saldo");
        System.out.println("4. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private static void realizarDeposito(BancoService bancoService) {
        System.out.println("\n--- DEPÓSITO ---");
        System.out.print("Ingrese número de cuenta: ");
        int cuenta = scanner.nextInt();
        System.out.print("Ingrese monto a depositar: ");
        double monto = scanner.nextDouble();

        try {
            bancoService.depositar(cuenta, monto);
            System.out.println("Depósito realizado con éxito");
        } catch (SQLException e) {
            System.err.println("Error al realizar el depósito: " + e.getMessage());
        }
    }

    private static void realizarExtraccion(BancoService bancoService) {
        System.out.println("\n--- EXTRACCIÓN ---");
        System.out.print("Ingrese número de cuenta: ");
        int cuenta = scanner.nextInt();
        System.out.print("Ingrese monto a extraer: ");
        double monto = scanner.nextDouble();

        try {
            bancoService.extraer(cuenta, monto);
            System.out.println("Extracción realizada con éxito");
        } catch (SQLException e) {
            System.err.println("Error al realizar la extracción: " + e.getMessage());
        }
    }

    private static void consultarSaldo(BancoService bancoService) {
        System.out.println("\n--- CONSULTA DE SALDO ---");
        System.out.print("Ingrese número de cuenta: ");
        int cuenta = scanner.nextInt();
        
        try {
            bancoService.consultarCuenta(cuenta);
        } catch (SQLException e) {
            System.err.println("Error al consultar el saldo: " + e.getMessage());
        }
    }
}