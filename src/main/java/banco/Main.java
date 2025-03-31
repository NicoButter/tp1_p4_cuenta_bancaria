/**
 * Sistema Bancario Básico - TP1 Ejercicio 4
 * <p>
 * Trabajo Práctico N°1 de la materia Laboratorio de Programación (2025) 
 * de la Licenciatura en Sistemas de la Universidad Nacional de la Patagonia Austral (UNPA-UARG).
 * </p>
 * <p>
 * Implementa un menú interactivo para operaciones bancarias con persistencia en MariaDB.
 * Motor: MariaDB en openSUSE (Victus 16)
 * </p>
 * 
 * @author Nicolas Butterfield
 * @version 1.1
 * @since Marzo 2025
 */

package banco;

import banco.service.BancoService;
import banco.config.DatabaseConfig;
import banco.config.DatabaseInitializer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    private static void limpiarPantalla() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
            }
        } catch (Exception e) {
            System.out.println("\n".repeat(50));
        }
    }

    private static void pausar() {
        System.out.println("\n" + "═".repeat(40));
        System.out.print("Presione Enter para volver al menú...");
        scanner.nextLine();
    }

    public static void main(String[] args) {

        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection == null) {
                System.err.println("No pude conectarme a tu base de datos :|, revisá tu configuración.");
                return;
            }

            BancoService bancoService = new BancoService(connection);
            boolean salir = false;

            while (!salir) {
                limpiarPantalla();
                mostrarMenu();
                try {
                    int opcion = scanner.nextInt();
                    scanner.nextLine(); 

                    switch (opcion) {
                        case 1 -> crearCuenta(bancoService);
                        case 2 -> listarCuentas(bancoService);
                        case 3 -> realizarOperacion(bancoService, "DEPÓSITO");
                        case 4 -> realizarOperacion(bancoService, "EXTRACCIÓN");
                        case 5 -> consultarSaldo(bancoService);
                        case 6 -> eliminarCuenta(bancoService);
                        case 7 -> mostrarMovimientosCuenta(bancoService);
                        case 8 -> filtrarMovimientosPorTipo(bancoService);
                        case 9 -> mostrarMovimientosCliente(bancoService);
                        case 10 -> resetearBaseDatos(bancoService);
                        case 11 -> salir = true;
                        default -> {
                            System.out.println("Opción no válida");
                            pausar();
                        }
                    }
                } catch (InputMismatchException e) {
                    System.err.println("Error: Ingrese un número válido");
                    scanner.nextLine();
                }
            }
            System.out.println("Sistema cerrado, hasta la próxima!");
        } catch (SQLException e) {
            System.err.println("No encuentro la base de datos, revisa tu configuración");
            e.printStackTrace();
        }
    }

    private static void mostrarMenu() {
        System.out.println("\n=== SISTEMA BANCARIO - TP1 - P4 ===");
        System.out.println("1. Crear cuenta nueva");
        System.out.println("2. Listar todas las cuentas");
        System.out.println("3. Depositar");
        System.out.println("4. Extraer");
        System.out.println("5. Consultar saldo");
        System.out.println("6. Eliminar cuenta");
        System.out.println("7. Mostrar movimientos de cuenta");
        System.out.println("8. Mostrar depósitos/extracciones");
        System.out.println("9. Mostrar movimientos por cliente");
        System.out.println("10. Resetear base de datos (DEBUG)");
        System.out.println("11. Salir");
        System.out.print("Seleccione opción: ");
    }

    private static void crearCuenta(BancoService bancoService) {
        System.out.println("\n--- CREAR CUENTA ---");

        try {
            System.out.print("Nombre del cliente?: ");
            String cliente = scanner.nextLine();

            System.out.print("Tipo de cuenta (A=Ahorro, C=Corriente)?: ");
            char tipo = scanner.nextLine().toUpperCase().charAt(0);

            System.out.print("Saldo inicial?: ");
            double saldo = scanner.nextDouble();
            scanner.nextLine();

            int numeroCuenta = bancoService.crearCuenta(cliente, saldo, tipo);
            System.out.println("¡Creaste la cuenta exitosamente! Númerode cuenta: " + numeroCuenta);
            pausar();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void listarCuentas(BancoService bancoService) {
        try {
            bancoService.listarTodasLasCuentas();
            pausar();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            pausar();
        }
    }

    private static void realizarOperacion(BancoService bancoService, String tipoOperacion) {
        System.out.println("\n--- " + tipoOperacion.toUpperCase() + " ---");
        try {
            System.out.print("Ingresá el número de cuenta a operar: ");
            int cuenta = scanner.nextInt();
            System.out.print("Ingresá monto: ");
            double monto = scanner.nextDouble();
            scanner.nextLine();

            boolean exito;
            switch (tipoOperacion) {
                case "DEPÓSITO" -> exito = bancoService.depositar(cuenta, monto);
                case "EXTRACCIÓN" -> exito = bancoService.extraer(cuenta, monto);
                default -> exito = false;
            }

            if (exito) {
                System.out.println(tipoOperacion + " realizado con éxito");
            } else {
                System.out.println("ERROR: No se pudo completar la " + tipoOperacion.toLowerCase());
            }
            pausar();

        } catch (InputMismatchException e) {
            System.err.println("Error: Ingrese valores numéricos válidos");
            scanner.nextLine();
            pausar();
        } catch (Exception e) {
            System.err.println("Error durante la operación: " + e.getMessage());
            e.printStackTrace();
            pausar();
        }
    }

    private static void consultarSaldo(BancoService bancoService) {
        System.out.println("\n--- CONSULTA DE SALDO ---");
        try {
            System.out.print("Ingrese número de cuenta: ");
            int cuenta = scanner.nextInt();
            scanner.nextLine();

            bancoService.consultarCuenta(cuenta);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        pausar();
    }

    private static void eliminarCuenta(BancoService bancoService) {
        System.out.println("\n--- ELIMINAR CUENTA ---");
        try {
            System.out.print("Ingrese número de cuenta a eliminar: ");
            int cuenta = scanner.nextInt();
            scanner.nextLine();

            if (bancoService.eliminarCuenta(cuenta)) {
                System.out.println("Cuenta eliminada exitosamente");
            }
            pausar();
        } catch (InputMismatchException e) {
            System.err.println("Error: Ingrese un número válido");
            scanner.nextLine();
            pausar();
        } catch (SQLException e) {
            System.err.println("Error al eliminar: " + e.getMessage());
            pausar();
        }
    }

    private static void mostrarMovimientosCuenta(BancoService bancoService) {
        System.out.println("\n--- MOVIMIENTOS DE CUENTA ---");
        try {
            System.out.print("Ingrese número de cuenta: ");
            int cuenta = scanner.nextInt();
            scanner.nextLine();

            bancoService.mostrarMovimientosCuenta(cuenta);
            pausar();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            pausar();
        }
    }

    private static void filtrarMovimientosPorTipo(BancoService bancoService) {
        System.out.println("\n--- FILTRAR MOVIMIENTOS POR TIPO ---");
        try {
            System.out.print("Ingrese número de cuenta: ");
            int cuenta = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Tipo a filtrar (D=Depósito, E=Extracción): ");
            char tipo = scanner.nextLine().toUpperCase().charAt(0);

            bancoService.filtrarMovimientosPorTipo(cuenta, tipo);
            pausar();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            pausar();
        }
    }

    private static void mostrarMovimientosCliente(BancoService bancoService) {
        System.out.println("\n--- MOVIMIENTOS POR CLIENTE ---");
        try {
            System.out.print("Ingrese nombre del cliente: ");
            String cliente = scanner.nextLine();

            bancoService.mostrarMovimientosCliente(cliente);
            pausar();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            pausar();
        }
    }

    private static void resetearBaseDatos(BancoService bancoService) {
        System.out.println("\n--- RESETEO DE BASE DE DATOS ---");
        System.out.println("ADVERTENCIA: Esto borrará todos los datos");
        System.out.print("¿Está seguro? (S/N): ");

        String confirmacion = scanner.nextLine().toUpperCase();
        if (confirmacion.equals("S")) {
            try {
                new DatabaseInitializer(DatabaseConfig.getConnection()).resetCompleto();
                System.out.println("Base de datos reseteada correctamente");
            } catch (SQLException e) {
                System.err.println(" Error al resetear: " + e.getMessage());
            }
        } else {
            System.out.println("Operación cancelada");
        }
        pausar();
    }

}
