package Comienzo.Tarea2;

import java.io.File;
import java.io.IOException;

public class Ejercicio1 {
    public static void main(String[] args) {
        try {
            mostrarDrectorioDeEjecucion("ServiciosYProcesos/src/Comienzo/Tarea2");
            mostrarDrectorioDeEjecucion("ServiciosYProcesos/src/Comienzo/Tarea1");
            mostrarEntornoEjecucion();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void mostrarDrectorioDeEjecucion(String ruta) throws IOException {
        // Creamos un proceso
        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/C", "dir");
        pb.inheritIO();
        // Le asignamos el directorio donde se va a ejecutar el proceso con .directory()
        pb.directory(new File(ruta));
        // Ejecutamos el proceso
        Process p = pb.start();
        // Mostramos el directorio donde se ejecuta el proceso
        System.out.println("Directorio por defecto del proceso: " + pb.directory());
    }

    public static void mostrarEntornoEjecucion() throws IOException {
        // Creamos un proceso
        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/C", "dir");
        pb.inheritIO();
        /* Obtenemos los elementos del .environment() y los mostramos, este sirve para mostrar y configurar
         con set y get el entorno para el proceso */
        System.out.println();
        pb.environment().forEach((key, value) -> System.out.println(key + ": " + value));
        System.out.println();
    }
}
