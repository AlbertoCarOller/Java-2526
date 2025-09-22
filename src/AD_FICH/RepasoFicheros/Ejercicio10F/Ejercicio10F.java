package AD_FICH.RepasoFicheros.Ejercicio10F;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ejercicio10F {
    public static void main(String[] args) {
        try {
            extraerNumeros(new File("src/AD_FICH/RepasoFicheros/Ejercicio10F/numerosDeTelefono.txt"),
                    new File("src/AD_FICH/RepasoFicheros/Ejercicio10F/numerosDeTelefonoCorrectos.txt"));
        } catch (RuntimeException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    /**
     * Extraemos los números de teléfono que están en el archivo
     * y los escribimos en el destino
     *
     * @param original archivo de donde vamos a extraer los números
     * @param destino  archivo donde vamos a escribir los números extraídos
     */
    public static void extraerNumeros(File original, File destino) {
        try (BufferedReader br = new BufferedReader(new FileReader(original));
             BufferedWriter bw = new BufferedWriter(new FileWriter(destino))) {
            List<String> coincidencias = new ArrayList<>();
            // Creamos un patter para buscar números de teléfono
            Pattern pattern = Pattern.compile("(?<Correo>(\\+?[0-9]{1,3}(\\s|-))?[0-9]{3}(\\s|-)[0-9]{3}(\\s|-)[0-9]{3})");
            br.lines().flatMap(l -> {
                // Vaciamos la lista para que no se escriban números repetidos
                coincidencias.clear();
                Matcher matcher = pattern.matcher(l);
                // Mientras encuentre coincidencias las guarda en la lista que posteriormente aplanamos
                while (matcher.find()) {
                    coincidencias.add(matcher.group("Correo"));
                }
                return coincidencias.stream();
            }).forEach(s -> {
                try {
                    // Escribimos en el nuevo archivo
                    bw.write(s);
                    bw.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}