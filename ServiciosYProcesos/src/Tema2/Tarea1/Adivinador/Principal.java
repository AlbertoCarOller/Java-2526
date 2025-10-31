package Tema2.Tarea1.Adivinador;

public class Principal {
    public static void main(String[] args) {
        try {
            // Generamos el número
            NumeroAzar numeroAzar = new NumeroAzar();
            numeroAzar.start();
            numeroAzar.join(); /* -> Necesario para que el main espere a que 'NumeroAzar' termine, es decir dé el número,
             el join hace que el hilo padre, en este caso el main, espere a que termine el hilo hijo*/
            int numeroParaAdivinar = numeroAzar.numeroAletorio;
            // Generamos varios hilos que van a intentar buscar el hilo
            Adivinador adivinador = new Adivinador(numeroParaAdivinar);
            Adivinador adivinador2 = new Adivinador(numeroParaAdivinar);
            Adivinador adivinador3 = new Adivinador(numeroParaAdivinar);
            Adivinador adivinador4 = new Adivinador(numeroParaAdivinar);
            // Comenzamos todos los hilos
            adivinador.start();
            adivinador2.start();
            adivinador3.start();
            adivinador4.start();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
