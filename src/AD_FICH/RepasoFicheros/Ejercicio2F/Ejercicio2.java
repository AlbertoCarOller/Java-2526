package AD_FICH.RepasoFicheros.Ejercicio2F;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public class Ejercicio2 {
    public static void main(String[] args) {
        String ruta = "src/AD_FICH/RepasoFicheros/Ejercicio2F/prueba.txt";
        if (args.length >= 1) {
            ruta = args[0];
        }
        comprobarRuta(new File(ruta));
    }

    public static void comprobarRuta(File file) {
        // Comprobamos que exista el fichero
        if (file.exists()) {
            // En caso de que sea un directorio entra
            if (file.isDirectory()) {
                System.out.println("Es un directorio");
                File[] archivos = file.listFiles();
                System.out.println("Archivos que contiene: " + Arrays.toString(archivos));

                // En caso de que sea file entra
            } else if (file.isFile()) {
                System.out.println("Es un fichero");
                System.out.println("Nombre: " + file.getName());
                System.out.println("Ruta: " + file.getAbsolutePath());
                System.out.println("Fecha modificacion: " + file.lastModified());
                try {
                    Files.readAllLines(file.toPath()).forEach(System.out::println);
                } catch (IOException e) {
                    System.out.println("Error al leer el fichero: " + e.getMessage());
                }
            }
        }
    }
}