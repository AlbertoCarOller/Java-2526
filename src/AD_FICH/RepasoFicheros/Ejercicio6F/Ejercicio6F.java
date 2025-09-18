package AD_FICH.RepasoFicheros.Ejercicio6F;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Ejercicio6F {
    public static void main(String[] args) {
        transformarABinario(new File("src/AD_FICH/RepasoFicheros/Ejercicio6F/shreck1.jpeg"),
                new File("src/AD_FICH/RepasoFicheros/Ejercicio6F/shreck1Bytes.txt"));
    }

    /* Las clases InputStreamReader y OutputStreamWriter traducen de bytes a caracteres, PERO NO
     * AL REVÉS */
    public static void transformarABinario(File leer, File destino) {
        try (InputStreamReader leerBytes = new InputStreamReader(new FileInputStream(leer));
             OutputStreamWriter escribirBytes = new OutputStreamWriter(new FileOutputStream(destino), StandardCharsets.UTF_8)) {
            // Creamos un array de char para no tener que leer de caracter en caracter
            char[] buffer = new char[1024];
            int bytesLeidos;
            while ((bytesLeidos = leerBytes.read(buffer)) != -1) {
                escribirBytes.write(buffer, 0, bytesLeidos);
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}