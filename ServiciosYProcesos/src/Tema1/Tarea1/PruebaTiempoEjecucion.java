package Tema1.Tarea1;

import java.io.IOException;

public class PruebaTiempoEjecucion {
    public static void main(String[] args) {
        try {
            comprobarEstado();
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void comprobarEstado() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/C", "java -jar C:\\Users\\Alberto" +
                ".DESKTOP-O1GC77M\\Desktop\\Java\\Java-2526\\ServiciosYProcesos\\src\\Comienzo\\Tarea1\\Contador10R.jar");
        Process p = pb.start();
        pb.inheritIO();
        // Mientras el programa esté vivo ejecutamos el while
        while (p.isAlive()) {
            // Imprimos si sigue vivo
            System.out.println("Alive: " + p.isAlive());
            // Esperamos 3 segundos
            Thread.sleep(3000);
        }
    }
}
