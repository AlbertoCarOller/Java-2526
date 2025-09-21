package AD_FICH.RepasoFicheros.Ejercicio8F;

import java.io.*;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ejercicio8F {
    public static void main(String[] args) {
        extraerGmail(new File("src/AD_FICH/RepasoFicheros/Ejercicio8F/gmail.txt"),
                new File("src/AD_FICH/RepasoFicheros/Ejercicio8F/gmailExtraido.txt"));
    }

    public static void extraerGmail(File origen, File destino) {
        try (BufferedReader br = new BufferedReader(new FileReader(origen));
             BufferedWriter bw = new BufferedWriter(new FileWriter(destino))) {

            // Creamos un pattern para buscar un correo
            Pattern pattern = Pattern.compile("(?<Correo>[\\p{LD}._+-]+@[\\p{LD}._+-]*(gmail|outlook|educaand)\\.(es|com))");
            br.lines().flatMap(l -> Arrays.stream(l.split("\\s+"))).filter(l -> {
                Matcher m = pattern.matcher(l);
                return m.matches();
            }).forEach(l -> {
                try {
                    bw.write(l);
                    bw.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (IOException | RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}