package Tema2;

public class Principal {
    static void main() {
        // Creamos diferentes corredores, vemos quién llegará al final, es decir a 29
        Corredor c1 = new Corredor();
        Corredor c2 = new Corredor();
        Corredor c3 = new Corredor();
        Corredor c4 = new Corredor();

        // Inicializamos los corredores
        c1.start();
        c2.start();
        c3.start();
        c4.start();
    }
}
