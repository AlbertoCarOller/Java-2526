package Tema3.EjercicioAdivinanzaMultichat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClienteChat {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        // Siempre se conectará al socket, el cuál es nuevo, no puede ser el mismo porque se cierra
        /*while (true) {*/
        /* Creamos el socket, la llamada del cliente hacia el servidor, ponemos el puerto al que
         * se va a conectar que es el del server socket, en este caso es 8888, y le damos un nombre
         * al cliente, en este caso 'localhost' */
        try (Socket socket = new Socket("localhost", 8888)) {
            // Utilizamos el tubo de escritura para poder enviar al servidor
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            /* Utilizamos el tubo de lectura para leer lo que nos llegue por el servidor,
             el cliente escribe y después lee para que no se quede el servidor esperando infinitamente*/
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Iniciamos el hilo que va a mostrar/leer el mensaje recibido
            HiloEscuchadorCliente escuchador = new HiloEscuchadorCliente(in);
            // Comenzamos el hilo (un mismo hilo solo se puede lanzar una vez)
            escuchador.start();
            // Hacemos el while dentro para no crear nuevas llamadas (socket)
            boolean fin = false;

            System.out.println("Introduce tu nombre de jugador:");
            out.println(sc.nextLine());

            while (!fin) {
                System.out.println("Introduce tu número (o 'fin' para salir):");
                String mensajeAEnviar = sc.nextLine();

                if (mensajeAEnviar != null) {
                    if (mensajeAEnviar.equalsIgnoreCase("fin")) {
                        System.out.println("Se cerró la conexión");
                        fin = true;
                        out.println("fin");
                    } else {
                        // Enviamos el número como String
                        out.println(mensajeAEnviar);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
        /*}*/
    }
}