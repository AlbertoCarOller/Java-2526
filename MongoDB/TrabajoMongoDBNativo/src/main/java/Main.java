import com.mongodb.MongoException;
import service.GestorTienda;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // 1. Inicializamos recursos fuera del bucle
        Scanner scanner = new Scanner(System.in);
        GestorTienda gestor;

        try {
            // Intentamos conectar a la BBDD antes de arrancar el menú
            gestor = new GestorTienda();
            System.out.println("✅ Conexión establecida con MongoDB.");
        } catch (IOException | MongoException e) {
            System.err.println("❌ Error CRÍTICO al conectar con la base de datos: " + e.getMessage());
            // Si no hay conexión, terminamos el programa porque no podemos hacer nada
            return;
        }

        // 2. El Bucle Principal
        int opcion = -1;
        do {
            try {
                // Mostrar el menú
                System.out.println("\n--- 🎮 TIENDA GAMING MONGO DB ---");
                System.out.println("1. Cargar Datos Semilla (Reset BBDD)");
                System.out.println("0. Salir");
                System.out.print("Selecciona una opción: ");

                // Leemos la opción (asumimos que escribe un número)
                opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1:
                        System.out.println("⏳ Cargando datos...");
                        gestor.cargarDatosSemilla();
                        System.out.println("✅ Datos cargados correctamente.");
                        break;
                    case 0:
                        System.out.println("👋 ¡Hasta luego!");
                        break;
                    default:
                        System.out.println("⚠️ Opción no reconocida.");
                }

            } catch (NumberFormatException e) {
                // Si el usuario escribe letras en vez de números
                System.err.println("❌ Error: Debes introducir un número.");
            } catch (Exception e) {
                // 🛡️ AQUÍ ESTÁ LA CLAVE: Atrapamos cualquier error de Mongo o lógica
                // Mostramos el error, pero NO hacemos 'return' ni 'break', 
                // así que el bucle continúa.
                System.err.println("❌ Ocurrió un error inesperado: " + e.getMessage());
                // e.printStackTrace(); // Descomenta esto si quieres ver más detalles del error
            }

        } while (opcion != 0);

        // Cerramos recursos al salir
        scanner.close();
    }
}