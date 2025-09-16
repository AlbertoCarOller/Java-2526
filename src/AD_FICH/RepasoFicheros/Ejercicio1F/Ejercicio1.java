package AD_FICH.RepasoFicheros.Ejercicio1F;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Ejercicio1 {
    public static void main(String[] args) {
        try {
            Path path = Path.of("src/AD_FICH/RepasoFicheros/Ejercicio1F/inventario.txt");
            mostrarInventario(path);
        } catch (Ejercicio1Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void mostrarInventario(Path inventario) throws Ejercicio1Exception {
        try(BufferedReader br = new BufferedReader(new FileReader(inventario.toFile()))) {
            /*
            // Guardamos la línea
            String linea;
            while ((linea = br.readLine()) != null) {
                System.out.println(linea);
            }*/

            /*
            // Mostramos las líenas con un flujo
            br.lines().forEach(System.out::println);*/

            List<String> lineas = Files.readAllLines(inventario);
            lineas.forEach(System.out::println);
        } catch (IOException e) {
            throw new Ejercicio1Exception(e.getMessage());
        }
    }
}
