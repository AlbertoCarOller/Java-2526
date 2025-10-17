package Tema2;

public class Corredor extends Thread {

    @Override
    public void run() {
        System.out.println(this.getName() + " ha despertado");
        for (int i = 0; i < 30; i++) {
            try {
                // Esperamos de 0 a 2999 milisegundos
                Thread.sleep((int) (Math.random() * 3000));
                System.out.println(this.getName() + " :" + i);

            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
