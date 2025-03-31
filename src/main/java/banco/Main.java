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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.InputMismatchException;
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
            boolean salir = false;

            while (!salir) {
                mostrarMenu();
                try {
                    int opcion = scanner.nextInt();
                    scanner.nextLine(); 

                    switch (opcion) {
                        case 1 -> crearCuenta(bancoService);
                        case 2 -> realizarDeposito(bancoService);
                        case 3 -> realizarExtraccion(bancoService);
                        case 4 -> consultarSaldo(bancoService);
                        case 5 -> eliminarCuenta(bancoService);
                        case 6 -> salir = true;
                        default -> System.out.println("Opción no válida");
                    }
                } catch (InputMismatchException e) {
                    System.err.println("Error: Ingrese un número válido");
                    scanner.nextLine();
                }
            }
            System.out.println("Saliendo del sistema...");
        } catch (SQLException e) {
            System.err.println("Error de conexión a la base de datos:");
            e.printStackTrace();
        }
    }

    private static void mostrarMenu() {
        System.out.println("\n=== SISTEMA BANCARIO ===");
        System.out.println("1. Crear cuenta nueva");
        System.out.println("2. Depositar");
        System.out.println("3. Extraer");
        System.out.println("4. Consultar saldo");
        System.out.println("5. Eliminar cuenta");
        System.out.println("6. Salir");
        System.out.print("Seleccione opción: ");
    }

    private static void crearCuenta(BancoService bancoService) {
        System.out.println("\n--- CREAR CUENTA ---");
        
        try {
            System.out.print("Nombre del cliente: ");
            String cliente = scanner.nextLine();
            
            System.out.print("Tipo de cuenta (A=Ahorro, C=Corriente): ");
            char tipo = scanner.nextLine().toUpperCase().charAt(0);
            
            System.out.print("Saldo inicial: ");
            double saldo = scanner.nextDouble();
            scanner.nextLine(); 
            
            int numeroCuenta = bancoService.crearCuenta(cliente, saldo, tipo);
            System.out.println("¡Cuenta creada exitosamente! Número: " + numeroCuenta);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void realizarOperacion(BancoService bancoService, String tipoOperacion) {
        System.out.println("\n--- " + tipoOperacion.toUpperCase() + " ---");
        try {
            System.out.print("Ingrese número de cuenta: ");
            int cuenta = scanner.nextInt();
            System.out.print("Ingrese monto: ");
            double monto = scanner.nextDouble();
            scanner.nextLine(); 

            switch (tipoOperacion) {
                case "DEPÓSITO" -> {
                    bancoService.depositar(cuenta, monto);
                    System.out.println("Depósito realizado con éxito");
                }
                case "EXTRACCIÓN" -> {
                    bancoService.extraer(cuenta, monto);
                    System.out.println("Extracción realizada con éxito");
                }
            }
        } catch (InputMismatchException e) {
            System.err.println("Error: Ingrese valores numéricos válidos");
            scanner.nextLine();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void realizarDeposito(BancoService bancoService) {
        realizarOperacion(bancoService, "DEPÓSITO");
    }

    private static void realizarExtraccion(BancoService bancoService) {
        realizarOperacion(bancoService, "EXTRACCIÓN");
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
    }

    private static void eliminarCuenta(BancoService bancoService) {
        System.out.println("\n--- ELIMINAR CUENTA ---");
        try {
            System.out.print("Ingrese número de cuenta a eliminar: ");
            int cuenta = scanner.nextInt();
            scanner.nextLine();
            
            if (bancoService.eliminarCuenta(cuenta)) {
                System.out.println("✅ Cuenta eliminada exitosamente");
            } else {
                System.out.println("❌ No se pudo eliminar: la cuenta no existe o tiene saldo distinto de cero");
            }
        } catch (InputMismatchException e) {
            System.err.println("Error: Ingrese un número válido");
            scanner.nextLine();
        } catch (SQLException e) {
            System.err.println("Error de base de datos: " + e.getMessage());
        }
    }
}

