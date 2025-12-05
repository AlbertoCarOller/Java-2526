package Tema3.EjercicioClienteServidor;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class HiloDelegadorServidor extends Thread {
    // Aceptamos el socket, que es la llamada, la petición del servidor
    Socket socket;
    Scanner sc;
    // El nombre del usuario
    String nombreUsuario;
    // Si se termina o no la conversación
    boolean fin;
    // Creamos el pw aquí arriba
    PrintWriter out;

    // Creamos el constructor
    public HiloDelegadorServidor(Socket socket) {
        this.socket = socket;
        sc = new Scanner(System.in);
        this.nombreUsuario = "";
        this.fin = false;
    }

    @Override
    public void run() {
        // CREAMOS LOS TUBOS DE ESCRITURA Y LECTURA
        // Nos creamos el tubo de lectura
        try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            /* Nos creamos el tubo de escritura, utilizamos el PrintWritter este es preferible en vez de
             * el BufferedWritter, porque este tiene la función de println y una opción de autoflush,
             * el autoflush, cada vez que se mete algo en el buffer de PrintWritter se envía, no espera
             * a que se llene el buffer  */
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Introduce tu nombre de usurio:");
            this.nombreUsuario = sc.nextLine();
            // Hacemos esto para que no se cierre el socket de la llamada, ya que se cierra en el try
            while (!fin) {
                // Leemos la línea, lo que nos llega del cliente
                String msg = br.readLine();
                // En caso de que el usuario que ha enviado el mensaje haya escrito fin, se termina la conversación
                if (msg != null) {
                    if (msg.equalsIgnoreCase("fin")) {
                        fin = true;
                        System.out.println(nombreUsuario + " cerró la conversación con el servidor");
                    } else {
                        // En caso de que no sea el fin de la conversación mostramos el mensaje y permitimos enviar mensaje
                        System.out.println("Mensaje recibido: " + msg);
                        // Una vez recibido el mensaje contestamos con el printWritter
                        //System.out.println("Escribe el mensaje (fin para terminar) " + nombreUsuario + ":");
                        //String mensajeAEnviar = sc.nextLine();
                        // Este escribe a los receptores, al resto de hilos, el mensaje recibido del cliente
                        ServidorChat.listaHilos.forEach(h -> h.enviarMensajes(nombreUsuario + ": " + msg));
                    }
                } else {
                    fin = true;
                    System.out.println(nombreUsuario + " cerró la conversación con el servidor");
                }
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Esta función va a enviar el mensaje al cliente
     *
     * @param mensaje el mensaje a enviar
     */
    public void enviarMensajes(String mensaje) {
        out.println(mensaje);
    }
}
