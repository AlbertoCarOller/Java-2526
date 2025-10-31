package Tema2.Tarea1;

public class Adivinador extends Thread {
    protected  int numABuscar;
    protected static boolean adivinado = false;
    protected int numeroGenerado;
    public Adivinador(int numeroGenerado) {
        this.numABuscar = numeroGenerado;
    }

    public void run() {
        System.out.println(this.getName() + " arranca motores...");
        while (!adivinado) {
            numeroGenerado = (int) ((Math.random() * 100) + 1);
            if (numeroGenerado == numABuscar) {
                adivinado = true;
                System.out.println(this.getName() + " ha descubierto el número " +  numeroGenerado);
            }
        }
    }
}
