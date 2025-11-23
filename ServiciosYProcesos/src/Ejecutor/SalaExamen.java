package Ejecutor;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SalaExamen {
    static Scanner sc = new Scanner(System.in);
    // Código para resetear el color, para que vuelva a tener el color original de la consola
    public static final String RESET = "\u001B[0m";

    // Colores de texto
    public static final String ROJO = "\u001B[31m";
    public static final String VERDE = "\u001B[32m";
    public static final String AZUL = "\u001B[34m";
    public static final String AMARILLO = "\u001B[33m";
    public static final String MORADO = "\u001B[35m";

    public static void main(String[] args) {
        System.out.println(VERDE + "Ingrese el número de profesores que hay" + RESET);
        int profesores = Integer.parseInt(sc.nextLine());
        while (profesores <= 0) {
            System.out.println("Vuelva a ingresar el número de profesores (debe ser mayor a 0)");
            profesores = Integer.parseInt(sc.nextLine());
        }
        try (ExecutorService executorService = Executors.newFixedThreadPool(profesores)) {
            /* Guardamos los futures y el número que corresponde al número del alumno
             en un mapa, los future cuando la tarea esté lista devuelve
             el resultado, en este caso el mensaje */
            Map<Future<Integer>, Integer> futures = new HashMap<>();
            // Solictamos el número de alumnos que hay
            System.out.println(AZUL + "Ingrese el número de alumnos que hay" + RESET);
            int alumnos = Integer.parseInt(sc.nextLine());
            while (alumnos <= 0) {
                System.out.println("Vuelva a introducir los alumnos que hay (debe ser mayor a 0)");
                alumnos = Integer.parseInt(sc.nextLine());
            }
            // Iniciamos un cronómetro desde este punto
            long inicio = System.currentTimeMillis();
            // Creamos los alumnos y se lo enviamos al pool de hilos (ExecutorService)
            for (int i = 1; i <= alumnos; i++) {
                futures.put(executorService.submit(new Alumno(i)), i);
            }
            futures.forEach((integerFuture, integer) -> {
                try {
                    System.out.println(ROJO + "El alumno " + integer + " ha sacado " + integerFuture.get() + RESET);
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println(e.getMessage());
                }
            });
            // Paramos el cronómetro por así decirlo
            long fin = System.currentTimeMillis();
            // Mostramos el tiempo que se tardó en que todos los alumnos terminaran los exámenes
            System.out.println(AMARILLO + ((fin - inicio) / 1000) + " segundos se ha tardado en realizar los exámenes" + RESET);
            System.out.println(MORADO + "La nota media del grupo es: " + calcularMedia(futures) + RESET);
            System.out.println(MORADO + "La nota máxima del grupo es: " + obtenerMax(futures) + RESET);
            System.out.println(MORADO + "La nota mínima del grupo es: " + obtenerMin(futures) + RESET);
        }
    }

    /**
     * Esta función calcula la media de todas las notas
     *
     * @param mapa el mapa que representa el id de los alumnos y sus notas
     * @return la nota media
     */
    public static double calcularMedia(Map<Future<Integer>, Integer> mapa) {
        return mapa.keySet().stream().mapToInt(f -> {
            try {
                return f.get();
            } catch (InterruptedException | ExecutionException e) {
                return 0;
            }
        }).average().orElse(0.0);
    }

    /**
     * Esta función devuelve la nota máxima de entre todos los alumnos
     *
     * @param mapa el mapa que representa el id de los alumnos y sus notas
     * @return la nota máxima
     */
    public static int obtenerMax(Map<Future<Integer>, Integer> mapa) {
        return mapa.keySet().stream().mapToInt(f -> {
            try {
                return f.get();
            } catch (InterruptedException | ExecutionException e) {
                return 0;
            }
        }).max().orElse(0);
    }

    /**
     * Esta función va a devolver la nota mínima de entre todos los alumnos
     *
     * @param mapa el mapa que representa el id de los alumnos y sus notas
     * @return la nota mínima
     */
    public static int obtenerMin(Map<Future<Integer>, Integer> mapa) {
        return mapa.keySet().stream().mapToInt(f -> {
            try {
                return f.get();
            } catch (InterruptedException | ExecutionException e) {
                return 0;
            }
        }).min().orElse(0);
    }
}
