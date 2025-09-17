package AD_FICH.RepasoFicheros.Ejercicio5F;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Ejercicio5F {
    public static void main(String[] args) {
        copiarImagen(new File("src/AD_FICH/RepasoFicheros/Ejercicio5F/shreck.jpeg"),
                new File("src/AD_FICH/RepasoFicheros/Ejercicio5F/shreckCopia.jpeg"));
    }

    public static void copiarImagen(File imagen, File destino) {
        try (FileInputStream leer = new FileInputStream(imagen);
             FileOutputStream escribir = new FileOutputStream(destino)) {
            // Creamos un buffer para no leer byte por byte (lento) y guardar los bytes leídos
            byte[] buffer = new byte[1024];
            // Cargamos los bytes leídos en datos
            int datos;
            // El .read() sobrecargado devuelve el número de bytes leídos y el .read() normal el byte directamente
            while ((datos = leer.read(buffer)) != -1) {
                // Escribimos de 0 en el buffer hasta donde lea 'datos'
                /* DUDA RESUELTA -> ¿Qué pasa si la imagen es de mayor de 1024 (el tamaño del array de bytes)?
                 * NO PASA NADA, ya que continúa leyendo después el resto, ¿Pero y cómo sabe donde se dejó de
                 * leer la última vez? Tiene un puntero interno por lo que no hay problema */
                escribir.write(buffer, 0, datos);
            }

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }
}