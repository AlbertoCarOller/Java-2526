package AD_FICH.RepasoFicheros.Ejercicio4F;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Ejercicio4F {
    public static void main(String[] args) {
        leerFichero(new File("src/AD_FICH/RepasoFicheros/Ejercicio3F/ficheroAEscribir.txt"));
    }

    public static void leerFichero(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            int contador = 0;
            /*
            while ((linea = br.readLine()) != null) {
                System.out.println(linea);*/
            while ((linea = br.readLine()) != null) {
                contador += linea.split("\\s+").length;
            }
            System.out.println("Hay " + contador + " palabras");

        } catch (IOException e) {
            System.out.println("Error al leer fichero: " + e.getMessage());
        }
    }
}