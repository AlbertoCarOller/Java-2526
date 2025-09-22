package AD_FICH.RepasoFicheros.Ejercicio9F;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Ejercicio9F {
    public static void main(String[] args) {
        contarCaracteres(new File("src/AD_FICH/RepasoFicheros/Ejercicio9F/ejemplo.txt"));
    }

    public static void contarCaracteres(File file) {
        try(InputStreamReader leer = new InputStreamReader(new FileInputStream(file))) {
            int caracteres = 0;
            int datos;
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[1024];
            while ((datos = leer.read(buffer)) != -1) {
                // Sumamos la cantidad de caracteres leídos
                caracteres += datos;
                // Apendamos los caracteres leídos
                sb.append(buffer, 0, datos);
                System.out.println(sb);
            }
            System.out.println("Hay " + caracteres + " caracteres");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}