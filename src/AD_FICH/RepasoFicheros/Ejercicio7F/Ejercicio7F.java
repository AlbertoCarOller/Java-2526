package AD_FICH.RepasoFicheros.Ejercicio7F;

import java.io.*;
import java.util.Arrays;

public class Ejercicio7F {
    public static void main(String[] args) {
        File origen = new File("src/AD_FICH/RepasoFicheros/Ejercicio7F/informe.txt");
        File destino = new File("src/AD_FICH/RepasoFicheros/Ejercicio7F/informeDatos.txt");

        contarLineas(origen, destino);
        contarLetras(origen, destino);
        contarCaracteres(origen, destino);
    }

    public static void contarLineas(File origen, File destino) {
        try (BufferedReader br = new BufferedReader(new FileReader(origen));
             BufferedWriter bw = new BufferedWriter(new FileWriter(destino, true))) {

            bw.write("Numero de lineas: " + br.lines().count());
            bw.newLine();

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void contarLetras(File origen, File destino) {
        try (BufferedReader br = new BufferedReader(new FileReader(origen));
             BufferedWriter bw = new BufferedWriter(new FileWriter(destino, true))) {

            bw.write("Número de palabras: " + br.lines()
                    .flatMap(l -> Arrays.stream(l.split("\\s+"))).count());
            bw.newLine();

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void contarCaracteres(File origen, File destino) {
        try (BufferedReader br = new BufferedReader(new FileReader(origen));
             BufferedWriter bw = new BufferedWriter(new FileWriter(destino, true))) {

            bw.write("Número de caracteres: " + br.lines().mapToInt(String::length).sum());
            bw.newLine();

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}