package Tema3.EjercicioClienteServidor;

import java.io.BufferedReader;
import java.io.IOException;

public class HiloEscuchadorCliente extends Thread {
    // Creamos el BufferedReader, realmente va a ser pasado
    public BufferedReader in;
    boolean fin = false;

    // Creamos el constructor
    public HiloEscuchadorCliente(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {
        while (!fin) {
            String mensajeRecibido = null;
            try {
                mensajeRecibido = in.readLine();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
            if (mensajeRecibido != null) {
                if (mensajeRecibido.equals("fin")) {
                    fin = true;
                    System.out.println("Se cerró la conexión con el servidor");
                } else {
                    // Imprimimos el mensaje recibido
                    System.out.println("Mensaje recibido de " + mensajeRecibido);
                }
            } else {
                fin = true;
                System.out.println("Se cerró la conexión con el servidor");
            }
        }
    }
}
