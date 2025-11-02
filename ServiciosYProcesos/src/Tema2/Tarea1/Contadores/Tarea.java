package Tema2.Tarea1.Contadores;

public class Tarea extends Thread {
    protected Contadores contadores;

    public Tarea(Contadores c) {
        this.contadores = c;
    }

    @Override
    public void run() {
        // Incrementamos primero el primer contador
        for (int i = 0; i < 100000000; i++) {
            contadores.incrementar1();
        }
        // Incrementamos el segundo contador
        for (int i = 0; i < 100000000; i++) {
            contadores.incrementar2();
        }
    }
}
