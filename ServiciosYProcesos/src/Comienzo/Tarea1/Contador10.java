package Comienzo.Tarea1;

public class Contador10 {
    public static void main(String[] args) {
        try {
            int contador = 1;
            while (contador <= 10) {
                // Esperamos 1 segundo
                Thread.sleep(1000);
                // Imprimimos el contador
                System.out.println(contador);
                contador++;
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}