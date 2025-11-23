package Ejecutor;

import java.util.concurrent.Callable;

public class Alumno implements Callable<Integer> {
    protected int id;

    public Alumno(int id) {
        this.id = id;
    }

    // La función call() devuelve un mensaje al terminar
    @Override
    public Integer call() throws Exception {
        /* Mensaje de que el alumno ha comenzado el examen y por el profesor que ha sido atendido,
        * el nombre lo averiguamos con Thread.currentThread().getName() (del profesor), con eso
        * obtenemos el identificador del hilo que está ejecutando el alumno en este caso */
        System.out.println("El alumno " + id + "  ha sido atendido por el profesor "
                + Thread.currentThread().getName());
        // Esperamos de 1 a 5 segundos antes de que el alumno termine el examen
        Thread.sleep((int) (Math.random() * 4000) + 1000);
        // Cuando se termina el tiempo (1-5) segundos, devolvemos la nota que ha sacado el alumno
        return (int) (Math.random() * 10) + 1;
    }
}
