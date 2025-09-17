package AD_FICH.RepasoFicheros.Ejercicio2F;

import java.io.File;
import java.util.Date;

public class InspectorFicheros {
    public static void main(String[] args) {
        // Pide a los alumnos que creen una carpeta "mis_cosas" en la raíz del proyecto
        // y dentro un fichero "prueba.txt" (puede estar vacío).
        String ruta = args[0];
        File fichero = new File(ruta);

        System.out.println("Inspeccionando la ruta: " + ruta);
        System.out.println("=========================================");

        if (fichero.exists()) {
            System.out.println("¡La ruta existe!");

            // ¿Es un fichero o un directorio?
            if (fichero.isFile()) {
                System.out.println("Es un fichero.");
                System.out.println("Nombre: " + fichero.getName());
                System.out.println("Ruta absoluta: " + fichero.getAbsolutePath());
                System.out.println("Tamaño (bytes): " + fichero.length());
                // La fecha se devuelve en milisegundos, la convertimos para que sea legible
                System.out.println("Última modificación: " + new Date(fichero.lastModified()));
            } else if (fichero.isDirectory()) {
                System.out.println("Es un directorio. Contenido:");
                File[] contenido = fichero.listFiles();
                if (contenido != null && contenido.length > 0) {
                    for (File elemento : contenido) {
                        System.out.println("- " + elemento.getName());
                    }
                } else {
                    System.out.println("El directorio está vacío.");
                }
            }
        } else {
            System.out.println("La ruta no existe.");
        }
    }
}