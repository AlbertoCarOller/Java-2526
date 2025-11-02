package Tema2.Tarea1.Contadores;

import java.util.ArrayList;
import java.util.List;

public class PrincipalContadores {
    static void main() {
        try {
            // Creamos el objeto contador
            Contadores contadores = new Contadores();
            // Creamos una lista de tareas donde guardar los 10 hilos
            List<Tarea> listaTareas = new ArrayList<>();
            // Creamos los 10 hilos en una listaTareas
            for (int i = 0; i < 10; i++) {
                listaTareas.add(new Tarea(contadores));
            }
            // Comezamos los hilos y le hacemos .join para que el hilo padre (main) espere a que terminen
            listaTareas.forEach(Thread::start);
            listaTareas.forEach(tarea -> {
                try {
                    tarea.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            // Mostramos el valor de los contadores
            System.out.println("Contador1: " + contadores.getContador1() + ", Contador2: " + contadores.getContador2());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
