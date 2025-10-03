package Comienzo.Tarea3;

public class Padre {
    public static void main(String[] args) {
        System.out.println("Empieza el padre");
        try {
            Hijo notepad = new Hijo();
            // Comenzamos el hilo
            notepad.start();
            // Mientras esté vivo se imprimirá
            while (notepad.isAlive()) {
                Thread.sleep(2000);
                System.out.println("Sigue vivo");
            }
            if (notepad.exitCode == 0) {
                System.out.println("El hijo ha finalizado");
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Termina el padre");
    }
}
