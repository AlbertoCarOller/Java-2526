package Tema2.Tarea1.Contadores;

class Contadores {
    // Contadores
    private long cont1 = 0;
    private long cont2 = 0;
    // Llaves, esto es importante, los synchronized necesitan un objeto que actúen como llave
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();

    public void incrementar1() {
        /* En caso de que la llave no esté cogida, es decir que otro hilo no esté incrementando el
         contador podrá incrementarlo */
        synchronized (lock1) {
            cont1++;
        }
    }

    public long getContador1() {
        synchronized (lock1) {
            return cont1;
        }
    }

    public void incrementar2() {
        synchronized (lock2) {
            cont2++;
        }
    }

    public long getContador2() {
        synchronized (lock2) {
            return cont2;
        }
    }
}