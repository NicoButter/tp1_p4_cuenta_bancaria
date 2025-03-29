/**
 * Sistema Bancario Básico - TP1 Ejercicio 4
 * <p>
 * Trabajo Práctico N°1 de la materia Laboratorio de Programación (2025) 
 * de la Licenciatura en Sistemas de la Universidad Nacional de la Patagonia Austral (UNPA-UARG).
 * </p>
 * 
 * Configuración de conexión a base de datos para el TP1-E4.
 * <p>
 * Desarrollo requerido para el Laboratorio de Programación (UNPA-UARG 2025).
 * Implementa el patrón Singleton para gestionar conexiones JDBC.
 * </p>
 *  * 
 * @author Nicolas Butterfield
 * @version 1.0
 * @since Marzo 2025
 */


package banco.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String URL = "jdbc:mariadb://localhost:3306/banco_db";
    private static final String USER = "banco";
    private static final String PASSWORD = "banco010203";

    public static Connection getConnection() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            
            return DriverManager.getConnection(
                URL, 
                USER, 
                PASSWORD
            );
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No encontré el driver JDBC de MariaDB :|");
            System.err.println("¿Incluiste el conector JDBC de MariaDB en tus dependencias? revisalo...");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error de conexión a la base de datos:");
            System.err.println("URL: " + URL);
            System.err.println("Usuario: " + USER);
            e.printStackTrace();
        }
        return null;
    }
}

