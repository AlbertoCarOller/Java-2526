package Comienzo.Tarea4;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Ejercicio2 {
    public static void main(String[] args) {
        try {
            if (args.length != 1) {
                throw new Ejercicio2Exception("Se debe de pasar un argumento");
            }
            comprobarPath(args[0]);
        } catch (Ejercicio2Exception | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Esta función va a comprobar que exista y sea un directorio
     * la ruta pasada, después mostramos por pantalla el contenido
     * que devuelve el comando y se coloca el número de línea por
     * cada línea
     *
     * @param ruta la ruta del directorio
     * @throws Ejercicio2Exception
     * @throws IOException
     */
    public static void comprobarPath(String ruta) throws Ejercicio2Exception, IOException {
        File f = new File(ruta);
        // Comprobamos que exista
        if (!f.exists()) {
            throw new Ejercicio2Exception("La ruta no existe");
        }
        // Comprobamos que sea un directorio
        if (!f.isDirectory()) {
            throw new Ejercicio2Exception("La ruta debe de corresponder a un directorio");
        }
        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "dir /A /O:-N", ruta);
        Process p = pb.start();
        // ImputStreamReader actúa como traductor, ya que p.getInputStream() devuelve bytes
        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            int cont = 1;
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(cont + ": " + line);
                cont++;
            }
        }
    }
}