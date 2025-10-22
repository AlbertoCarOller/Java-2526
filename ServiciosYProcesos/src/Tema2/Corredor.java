package Tema2;

public class Corredor extends Thread {

    public static boolean acabo;

    public Corredor() {
        acabo = false;
    }

    @Override
    public void run() {
        System.out.println(this.getName() + " ha despertado");
        for (int i = 0; i < 30; i++) {
            try {
                // Esperamos de 0 a 2999 milisegundos
                Thread.sleep((int) (Math.random() * 3000));
                System.out.println(this.getName() + " :" + i);
                // Se comprueba si ha ganado
                if (!acabo && i == 29) {
                    System.out.println(this.getName() + " HA GANADO!!!");
                    acabo = true;
                }

            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
