package Tema2.Tarea1.Adivinador;

public class NumeroAzar extends Thread {
    protected int numeroAletorio;

    public void run() {
        // Generámos un número entre 1 y 100
        numeroAletorio = (int) ((Math.random() * 100) + 1);
        System.out.println("El número a adivinar es: " + numeroAletorio);
    }
}
