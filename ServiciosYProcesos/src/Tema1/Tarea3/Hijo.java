package Tema1.Tarea3;

import java.io.IOException;

// Esta clase extiende de 'Thread' que es un hilo
public class Hijo extends Thread {
    int exitCode;

    /* NO HACER DENTRO DEL CONSTRUCTOR UN PROCESO Y QUE ESTÉ EN ESPERA PORQUE SI EL OBJETO NO SE TERMINA
     DE CREAR EN EL PADRE, BLOQUEA AL PADRE */

    // Esta función va a ejecutar en este caso un proceso, el notepad
    @Override
    public void run() {
        System.out.println("Iniciando notepad");
        // Creamos un proceso al llamar al constructor
        ProcessBuilder pb = new ProcessBuilder("notepad.exe");
        Process p;
        try {
            p = pb.start();
            // Esperamos hasta que se cierre, hasta que muera el proceso
            exitCode = p.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}