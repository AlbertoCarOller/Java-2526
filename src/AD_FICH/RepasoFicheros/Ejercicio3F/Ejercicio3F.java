package AD_FICH.RepasoFicheros.Ejercicio3F;

import java.io.*;

public class Ejercicio3F {
    public static void main(String[] args) {
        String ruta = "src/AD_FICH/RepasoFicheros/Ejercicio3F/ficheroAEscribir.txt";
        if (args.length >= 1) {
            ruta = args[0];
        }
        escribir(new File(ruta));
    }

    public static void escribir(File file) {
        try(BufferedWriter bw = new  BufferedWriter(new FileWriter(file, true))) {
            // Escribimos en el fichero
            bw.write("Ejercicio 3F, probando escritura\n");

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
