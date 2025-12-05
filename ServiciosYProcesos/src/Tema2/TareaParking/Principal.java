package Tema2.TareaParking;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

class Principal {
    public static void main(String[] args) {
        // Semaphore -> Indica los hilos que pueden entrar, en este caso hay 10 plazas, es decir caben 10 (hilos) coches a la vez
        Semaphore plaza = new Semaphore(10);
        // Comprobamos si es el segundo recorrido
        for (int x = 0; x < 20; x++) { // Creamos 40 coches
            Coche hiloCoche = new Coche("coche numero: " + (x + 1), plaza);
            Thread th = new Thread(hiloCoche);
            th.start();
        }
        for (int i = 0; i < 5; i++) { // Creamos 10 furgonetas
            Furgoneta hilofurgoneta = new Furgoneta("furgoneta numero: " + (i + 1), plaza);
            Thread th = new Thread(hilofurgoneta);
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
            Thread.sleep(100 + aleCirculando * 100L);
            //sem.acquire(); // acquire -> para que solo entren los coches que caben, cuando un hilo quiere entrar intenta coger permiso
            /* tryAcquire(tiempo a tardar, unidad del tiempo) -> Esta función hace que el hilo, en este caso el coche, intente
             * entrar en el semáforo, lo intentará el periodo de tiempo marcado, en caso de que no lo consiga, dejará de
             * intentar entrar */
            boolean entrada = sem.tryAcquire(((int) (Math.random() * 20)) + 10, TimeUnit.SECONDS);
            System.out.println("------------" + sem.getQueueLength() + " coches en la cola"); /* getQueueLength -> Muestra el número
             de coches en la cola */
            // En caso de que el coche (hilo) entre al parking (semaphore)
            if (entrada) {
                // Guardamos la matrícula en el array de parking
                Parking.guardarCoche(numeroCoche);
                System.out.println(sem.availablePermits() + " plazas libres"); /* availablePermits -> Muestra las huecos
             disponibles para que entre los hilos */
                Thread.sleep(1000 + aleParking * 100L);
                System.out.println(numeroCoche + " sale del parking");
                // Quitamos la matrícula del coche porque sale del parking
                Parking.eliminarCoche(numeroCoche);
                System.out.println(sem.availablePermits() + " plazas libres");
                System.out.println("------------" + sem.getQueueLength() + " vehículos en la cola");
                sem.release(); // release -> Para dejar pasar un hilo del Semaphore, en este caso dejar pasar a un coche
            } else {
                System.out.println("El coche " + numeroCoche + " ha esperado y se ha cansado, se va para casa");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Furgoneta implements Runnable {
    String numeroFurgoneta;
    Semaphore sem;
    int aleParking = (int) (Math.random() * 100); // Simula el tiempo que se queda la furgoneta en el parking
    int aleCirculando = (int) (Math.random() * 50); // Simula el tiempo de llegada de la furgoneta de casa al parking

    // El constructor de la furgoneta
    public Furgoneta(String numeroFurgoneta, Semaphore sem) {
        this.numeroFurgoneta = numeroFurgoneta;
        this.sem = sem;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(100 + aleCirculando * 100L);
            //sem.acquire(); // acquire -> para que solo entren los coches que caben, cuando un hilo quiere entrar intenta coger permiso
            /* tryAcquire(plazas que va a ocupar, tiempo a tardar, unidad del tiempo) -> Esta función hace que el hilo, en este caso la furgoneta, intente
             * entrar en el semáforo, lo intentará el periodo de tiempo marcado, en caso de que no lo consiga, dejará de
             * intentar entrar. En este caso la función cambia respecto a la anterior si nos fijamos acepta un tercer parámetro
             * al pricnipio que es el número de plazas que va a ocupar el hilo que está esperando para entrar (la furgoneta) */
            boolean entrada = sem.tryAcquire(2, ((int) (Math.random() * 20)) + 10, TimeUnit.SECONDS);
            System.out.println("------------" + sem.getQueueLength() + " furgoneta en la cola"); /* getQueueLength -> Muestra el número
             de vehículos en la cola */
            // En caso de que la furgoneta (hilo) entre al parking (semaphore)
            if (entrada) {
                // Guardamos la matrícula en el array de parking
                Parking.guardarFurgoneta(numeroFurgoneta);
                System.out.println(sem.availablePermits() + " plazas libres"); /* availablePermits -> Muestra las huecos
             disponibles para que entre los hilos */
                Thread.sleep(1000 + aleParking * 100L);
                System.out.println(numeroFurgoneta + " sale del parking");
                // Quitamos la matrícula del coche porque sale del parking
                Parking.eliminarFurgoneta(numeroFurgoneta);
                System.out.println(sem.availablePermits() + " plazas libres");
                System.out.println("------------" + sem.getQueueLength() + " vehículos en la cola");
                sem.release(2); /* release -> Para dejar pasar un hilo del Semaphore, en este caso dejar pasar a un vehículo,
                le tenemos que indicar al release que pueden entrar dos plazas */
            } else {
                System.out.println("La furgoneta " + numeroFurgoneta + " ha esperado y se ha cansado, se va para casa");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}