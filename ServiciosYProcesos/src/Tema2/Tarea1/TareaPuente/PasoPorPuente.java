package Tema2.Tarea1.TareaPuente;

/**
 *
 * programa que controle el paso de personas por un puente, siempre en la misma
 * dirección, para que se cumplan las siguientes restricciones. No pueden pasar
 * más de tres personas a la vez, y no puede haber más de 200 kg de peso en
 * ningún momento. En un sistema real, se podría obtener la información de las
 * personas que llegan y de su peso mediante sensores. Para realizar la
 * simulación, se va a modelar las personas como hilos. El tiempo entre la
 * llegada de dos personas es aleatorio entre 1 y 30 segundos,
 * para pasar entre 10 y 50 segundos, y las personas tienen un peso aleatorio
 * entre 40 y 120 kg.
 *
 */

import java.util.Random;

class Puente {  // Estado, objeto compartido entre los hilos

    private static final int PESO_MAXIMO = 200; // -> El peso máximo que puede soportar el puente
    private static final int MAX_PERSONAS_EN_SENTIDO = 3; // -> Las personas máximas en un sentido
    private int peso = 0; // -> Peso actual del puente
    private int numPersonas = 0; // -> Personas cruzando el puente
    public int estadoPuente = 0; // -> El estado del puente: 0 si está vacío, 1 si es norte y 2 si es sur

    synchronized public int getPeso() {
        return peso;
    }

    synchronized public int getNumPersonas() {
        return numPersonas;
    }

    // Esto devolverá el estado del puente
    synchronized public int getEstadoPuente() {
        return estadoPuente;
    }

    synchronized public void setEstadoPuente(int estadoPuente) {
        this.estadoPuente = estadoPuente;
    }

    /**
     * Esta función básicamente devolverá la autorización para ver una
     * persona si puede cruzar o por lo contrario debe de esperar
     *
     * @param persona la persona a comprobar
     * @return boolean, la autorización
     */
    synchronized public boolean autorizacionPaso(Persona persona) {

        boolean result;

        /* Se comprueba que el peso del puente y la persona no supere el máximo y que no esté completamente
         ocupado por el número de personas */ // TODO: añadir condición del tipo
        if (this.peso + persona.getPeso() <= Puente.PESO_MAXIMO && this.numPersonas < Puente.MAX_PERSONAS_EN_SENTIDO &&
                // Comprobación de que el puente esté vacío o que la persona del mismo tipo que el puente
                (this.estadoPuente == 0 || persona.getTipo() == this.estadoPuente)) {
            // Actualizamos el tipo del puente
            this.setEstadoPuente(persona.getTipo());
            this.numPersonas++;
            this.peso += persona.getPeso();
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    /**
     * Esta función quitará el peso de la persona del puente
     * y restará el número de personas que había en el puente
     *
     * @param persona la persona, se utiliza pa quitar el puente el peso concreto
     */
    synchronized public void terminaPaso(Persona persona) {
        // Quita el peso del puente
        this.peso -= persona.getPeso();
        // Quita una persona del puente
        this.numPersonas--;
        // Comprobamos si hay personas en el puente para cambiar el estado a vacío antes de llamar
        if (this.getNumPersonas() == 0) {
            // Cambiamos el estado a 0, no hay nadie
            this.setEstadoPuente(0);
        }
    }
}

class Persona implements Runnable {
    private final Puente puente;

    private final String idPersona;
    private final int peso;
    private final int tMinPaso, tMaxPaso;
    private int tipo; // -> El tipo de persona, es decir, si es 1 será norte, si es 2 será sur

    public int getPeso() {
        return peso;
    }

    // Para obtener el tipo de persona, es decir, si es 1 (Norte) o es 2 (Sur)
    public int getTipo() {
        return tipo;
    }

    Persona(Puente puente, int peso, int tMinPaso, int tMaxPaso, String idP) {
        this.puente = puente;
        this.peso = peso;
        this.tMinPaso = tMinPaso;
        this.tMaxPaso = tMaxPaso;
        this.idPersona = idP;
        this.tipo = (int) (Math.random() * 2) + 1; // -> Generamos un número entre 1 y 2 aleatoriamente;
    }

    @Override
    public void run() {

        System.out.printf("- %s de %d kg quiere cruzar, en puente %d kg, %d persona%s.\n",
                this.idPersona, this.peso, puente.getPeso(), puente.getNumPersonas(), puente.getNumPersonas() != 1 ? "s" : "");

        // Espera para conseguir autorización
        boolean autorizado = false;
        // Mientras que no esté autorizado, intenta llamar a su autorización para ver cuando tiene
        while (!autorizado) {
            synchronized (this.puente) {
                autorizado = this.puente.autorizacionPaso(this);
                // Si no está autorizado espera
                if (!autorizado) {
                    try {
                        System.out.printf("# %s de tipo %d debe esperar.\n", this.idPersona, this.tipo);
                        this.puente.wait();
                    } catch (InterruptedException ex) {
                        System.out.printf("Interrupción mientras %s espera para cruzar.\n", this.idPersona);
                    }
                }
            }
        }

        System.out.printf("> %s de tipo %d con peso %d puede cruzar, puente soporta peso %d, con %d personas.\n",
                this.idPersona, this.tipo, this.getPeso(), this.puente.getPeso(), puente.getNumPersonas(), this.puente.getNumPersonas() != 1 ? "s" : "");

        // Pasa al puente, y tarda un tiempo en cruzar
        Random r = new Random();
        int tiempoPaso = this.tMinPaso + r.nextInt(this.tMaxPaso - this.tMinPaso + 1);
        try {
            System.out.printf("%s de tipo %d va a tardar tiempo %d en cruzar.\n", this.idPersona, this.tipo, tiempoPaso);
            Thread.sleep(1000L * tiempoPaso);
        } catch (InterruptedException ex) {
            System.out.printf("Interrupción mientras %s de tipo %d pasa.\n", this.idPersona, this.tipo);
        }

        // Sale del puente
        synchronized (this.puente) {
            this.puente.terminaPaso(this);
            System.out.printf("< %s sale del puente, puente soporta peso %d, %d persona%s.\n",
                    this.idPersona, this.puente.getPeso(), this.puente.getNumPersonas(), this.puente.getNumPersonas() != 1 ? "s" : "");
            // Una vez que una persona ha salido del puente, notifica a las demás, el más rápido entrará
            puente.notifyAll(); /* Se necesita el notifyAll() para avisar a todas las personas disponibles.
       podría haber una perdida de llamada, ya que si un hilo que pesa demasiado y no puede entrar
       se queda esperando a que salga alguien del puente no hay nadie en el puente, puesto que la última
        que salió le dió el aviso a él, entonces ya no hay más avisos, dormirán para siempre*/
        }
    }
}

public class PasoPorPuente {

    public static void main(String[] args) {

        final Puente puente = new Puente();

        int tMinParaLlegadaPersona = 1;
        int tMaxParaLlegadaPersona = 30;
        int tMinPaso = 10;
        int tMaxPaso = 50;
        int minPesoPersona = 40;
        int maxPesoPersona = 120;

        System.out.println(">>>>>>>>>>>> Comienza simulación.");
        Random r = new Random();
        int idPersona = 1;

        while (true) {

            int tParaLlegadaPersona = tMinParaLlegadaPersona + r.nextInt(
                    tMaxParaLlegadaPersona - tMinParaLlegadaPersona + 1);
            int pesoPersona = minPesoPersona + r.nextInt(
                    maxPesoPersona - minPesoPersona + 1);

            System.out.printf("Siguiente persona llega en %d segundos.\n", tParaLlegadaPersona);

            try {
                Thread.sleep(1000 * tParaLlegadaPersona);
            } catch (InterruptedException ex) {
                System.out.printf("Interrumpido proceso principal");
            }

            Thread hiloPersona = new Thread(new Persona(puente, pesoPersona, tMinPaso, tMaxPaso, "P" + idPersona));
            hiloPersona.start();

            idPersona++;

        }

    }

}
