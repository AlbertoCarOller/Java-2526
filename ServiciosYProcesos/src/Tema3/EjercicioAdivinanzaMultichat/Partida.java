package Tema3.EjercicioAdivinanzaMultichat;

import java.util.Random;

public class Partida {
    // El númeero secreto a adivinar
    private int numeroSecreto;
    // Si ha finalizado o no el juego
    private boolean finalizado;
    // El nombre del ganador
    private String ganador;

    public Partida() {
        // Generamos el número aleatorio entre 0 y 100
        Random rand = new Random();
        this.numeroSecreto = rand.nextInt(101);
        // Indicamos que la partida todavía no ha finalizado
        this.finalizado = false;
        // El nombre del ganador al principio estará vacío
        this.ganador = "";
        // Imprimimos el número en el servidor para saber cuál es
        System.out.println("Número secreto generado: " + numeroSecreto);
    }

    /**
     * Esta función va a comprobar si el número introducido por
     * el jugador es correcto o no
     *
     * @param numero  el número introducido
     * @param jugador el nombre del jugador
     * @return Si es mayor, menor o es el correcto
     */
    public synchronized String comprobarIntento(int numero, String jugador) {
        // En caso de que haya finalizado el juego se muestra
        if (finalizado) {
            return "JuegoTerminado";
        }

        // En caso de que el número sea igual al número secreto se finaliza el juego
        if (numero == numeroSecreto) {
            finalizado = true;
            ganador = jugador;
            return "Correcto";
        } else if (numero < numeroSecreto) {
            return "Mayor";
        } else {
            return "Menor";
        }
    }

    /**
     * Esta función va comprobar si se ha finalizado
     * el juego o no
     * @return si ha finalizado o no
     */
    public boolean esFinalizado() {
        return finalizado;
    }
}