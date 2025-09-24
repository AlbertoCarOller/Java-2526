package Comienzo;

import java.io.IOException;

public class Ejemplo2 {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("No se puede ejecutar sin argumentos");
        } else {
            try {
                // Creamos el proceso que en este caso se pasará por argumentos
                ProcessBuilder pb = new ProcessBuilder(args);
                // Creamos una entrada y salida estandar para el proceso que hereda del main
                pb.inheritIO();
                Process p = pb.start();
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
