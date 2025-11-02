package Tema2.Tarea1.IncrementarYDecrementar;

class Contador {
    private int cuenta = 0;
    protected final int MAX = 20;

    Contador(int valorInicial) {
        this.cuenta = valorInicial;
    }

    synchronized public int getCuenta() {
        return cuenta;
    }

    /* Aquí nos fijamos, vemos que no se está pasando una llave a los synchronized (al menos visualmente)
     * pero al ponerlo así lo que le está pasando es su propio objeto (this) como llave, la diferencia entre
     * pasarte a ti mismo como llave o pasar un objeto dedicado, la principal diferencia es que con un
     * objeto dedicado es mucho más seguro y no bloquea a otras funciones, por ejemplo en este caso,
     * tenemos incrementar y decrementar, si tuviéramos llaves dedicadas para cada una, puede decrementar
     * e incrementar al mismo tiempo, pero no pisarse los hilos del mismo tipo */
    synchronized public int incrementa() {
        this.cuenta++;
        return cuenta;
    }

    synchronized public int decrementa() {
        this.cuenta--;
        return cuenta;
    }

}

class HiloIncr implements Runnable {

    private final String id;
    private final Contador cont;

    HiloIncr(String id, Contador c) {
        this.id = id;
        this.cont = c; // -> El contador
    }

    @Override
    public void run() {
        while (true) {
            /* Parece que se bloquea, pero NO, como dijimos anteriormente la llave solo se puede utilizar una vez
             * a la misma vez, aquí si nos fijamos estamos cogiendo la llave para este bloque synchronized por lo que
             * está bloqueada hasta que termine este bloque y no se pueda utilizar .incrementa() que utiliza la misma
             * llave para ejecutar su incremento, PERO Java es inteligente y sabe que este bloque utiliza ya esa llave
             * por lo que sabe que tiene la llave y va a permitir hacer el incremento */
            synchronized (this.cont) {
                // Si la cuenta llega al máximo, espera a que reste
                while (cont.getCuenta() == cont.MAX) {
                    try {
                        this.cont.wait(); // -> Espera a que reste
                    } catch (InterruptedException e) {
                    }
                }
                /* Se utiliza el mismo objeto Contador en el synchronized de este hilo tanto como en el de decremento,
                 * porque la llave debe estar asociada a su misma sala de espera, por lo que este .notify() avisa
                 * al .wait() del hilo decremental, el notify tiene efecto en el wait del otro gilo porque se
                 * utiliza la misma llave (el contador) para dar estos avisos, es una interconexión que hay entre ellos
                 * al ser del mismo objeto llave */
                this.cont.incrementa(); // -> Incrementa en 1 el valor del contador
                this.cont.notifyAll(); // -> Avisa una vez se ha incrementado
                System.out.printf("Hilo %s incrementa, valor contador: %d\n", this.id,
                        this.cont.getCuenta());
            }
            try {
                Thread.sleep(1); // -> Espera para no acaparar la llave, cuando ya no tenemos la llave
            } catch (InterruptedException e) {
            }
        }
    }
}

class HiloDecr implements Runnable {

    private final String id;
    private final Contador cont;

    HiloDecr(String id, Contador c) {
        this.id = id;
        this.cont = c; // -> El contador
    }

    @Override
    public void run() {
        while (true) {
            synchronized (this.cont) {
                /* Se pone un While en vez de un if por UNA SOLA RAZÓN MUY CONCRETA, pongámonos en la siguiente
                 * situación, tenemos el contador en 0, esperamos dentro del while, esperamos con .wait(),
                 * se incrementa, pero en ese mismo instante después de incrementarse se puede "colar" otro
                 * hilo decrementador, que sea 0 otra vez y cuando deja de esperar el hilo decrementador del
                 * principio se encuentra con que es 0 y si fuera un if, no volvería a esperar, pasaría abajo
                 * y decrementaría, ahora sería -1, cosa que no podemos permitir según el enunciado */
                while (this.cont.getCuenta() < 1) { // -> Comprueba que sea mayor o igual a 1 el contador
                    System.out.printf("!!! Hilo %s no puede decrementar, valor contador: %d\n",
                            this.id, this.cont.getCuenta());
                    try {
                        this.cont.wait(); // -> En caso de que entre, espera a que se incremente el contador
                    } catch (InterruptedException ex) {
                    }
                }
                this.cont.decrementa(); // -> Decrementa en 1 el valor del contador
                this.cont.notifyAll(); /* -> Notifica cuando resta, se utiliza este en vez de notify simple por
                 muchas razones de bloqueo, como que por ejemplo se hayan dormido 9 de los hilos de un tipo
                 como los de decremento y que no haya en la sala de espera ningún incrementador por lo que cuando
                 se llama a despertar a alguien, siempre sean decrementadores y se atasque, al llamar a todos
                 evitas que se acumulen en la sala de espera y se puedan acumular solo de un tipo*/
                System.out.printf("Hilo %s decrementa, valor contador: %d\n", this.id,
                        this.cont.getCuenta());
            }
            try {
                Thread.sleep(1); /* -> Esperamos 50 milisegundos para que los decrementadores no acaparen la llave,
                 cuando ya no la tenemos */
            } catch (InterruptedException e) {
            }
        }
    }
}

class HilosIncDec {

    private static final int NUM_HILOS_INC = 10; // -> número de hilos incrementadores
    private static final int NUM_HILOS_DEC = 10; // -> número de hilos decrementadores

    public static void main(String[] args) {
        // Se crea un nuevo contador, con un valor inicial de 0
        Contador c = new Contador(0);
        // Se crea un array de hilos, aquí irán los incrementadores
        Thread[] hilosInc = new Thread[NUM_HILOS_INC];
        // Se crea 10 hilos incrementadores y se guardan en el array
        for (int i = 0; i < NUM_HILOS_INC; i++) {
            Thread th = new Thread(new HiloIncr("INC" + i, c));
            hilosInc[i] = th;
        }
        // Comienza los hilos incrementadores
        for (int i = 0; i < NUM_HILOS_INC; i++) {
            hilosInc[i].start();
        }
        // Se crea un array de hilos decrementadores
        Thread[] hilosDec = new Thread[NUM_HILOS_DEC];
        // Se crean los hilos decrementadores y se meten en el array
        for (int i = 0; i < NUM_HILOS_DEC; i++) {
            Thread th = new Thread(new HiloDecr("DEC" + i, c));
            hilosDec[i] = th;
        }
        // Se inicializan los hilos decrementadores
        for (int i = 0; i < NUM_HILOS_DEC; i++) {
            hilosDec[i].start();
        }
    }
}