package Tema2.TareaParking;

import java.util.concurrent.Semaphore;

class Principal {
    public static void main(String[] args) {
        // Semaphore -> Indica los hilos que pueden entrar, en este caso hay 10 plazas, es decir caben 10 (hilos) coches a la vez
        Semaphore plaza = new Semaphore(10);
        for (int x = 0; x < 40; x++) { // Creamos 40 coches
            Coche hiloCoche = new Coche("coche numero: " + (x + 1), plaza);
            Thread th = new Thread(hiloCoche);
            th.start();
        }
    }
}

class Coche implements Runnable {
    String numeroCoche; // Mensaje del número del coche
    Semaphore sem, pintar; /* sem -> Referencia al semáforo principal, los objets necesitan tener referencia a este
     para poder entrar o salir */
    int aleParking = (int) (Math.random() * 100); // Simula el tiempo que se queda el coche en el parking
    int aleCirculando = (int) (Math.random() * 50); // Simula el tiempo de llegada del coche de casa al parking

    // El constructor del coche
    public Coche(String numeroCoche, Semaphore sem) {
        this.numeroCoche = numeroCoche;
        this.sem = sem;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(100 + aleCirculando * 100);
            sem.acquire(); // acquire -> para que solo entren los coches que caben, cuando un hilo quiere entrar intenta coger permiso
            System.out.println("------------" + sem.getQueueLength() + " coches en la cola"); /* getQueueLength -> Muestra el número
             de coches en la cola */
            System.out.println(numeroCoche + " entra al parking");
            System.out.println(sem.availablePermits() + " plazas libres"); /* availablePermits -> Muestra las huecos
             disponibles para que entre los hilos */
            Thread.sleep(1000 + aleParking * 100);
            System.out.println(numeroCoche + " sale del parking");
            System.out.println(sem.availablePermits() + " plazas libres");
            System.out.println("------------" + sem.getQueueLength() + " coches en la cola");
            sem.release(); // release -> Para soltar un hilo del Semaphore, en este caso soltar un coche
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}