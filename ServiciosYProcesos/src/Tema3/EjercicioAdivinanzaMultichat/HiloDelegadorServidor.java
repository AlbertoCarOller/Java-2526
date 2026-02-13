package Tema3.EjercicioAdivinanzaMultichat;

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

    // Objeto compartido de la partida
    Partida partida;
    // Contador de intentos
    int intentos;

    // Creamos el constructor
    public HiloDelegadorServidor(Socket socket, Partida partida) {
        this.socket = socket;
        this.partida = partida;
        this.intentos = 0;
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
            System.out.println("Esperando a que introduzca el nombre el usuario...");
            this.nombreUsuario = br.readLine();

            // Hacemos esto para que no se cierre el socket de la llamada, ya que se cierra en el try
            while (!fin && !partida.esFinalizado() && intentos < 5) {
                // Leemos la línea, lo que nos llega del cliente
                String msg = br.readLine();

                // En caso de que el usuario que ha enviado el mensaje haya escrito fin, se termina la conversación
                if (msg != null) {
                    if (msg.equalsIgnoreCase("fin")) {
                        fin = true;
                        System.out.println(nombreUsuario + " cerró la conversación con el servidor");
                    } else {
                        // En caso de que no sea el fin de la conversación mostramos el mensaje y permitimos enviar mensaje
                        System.out.println("Intento recibido de " + nombreUsuario + ": " + msg);

                        try {
                            int numero = Integer.parseInt(msg);
                            intentos++;

                            // Comprobamos el mensaje si ha acertado el usuario
                            String resultado = partida.comprobarIntento(numero, nombreUsuario);

                            // Una vez recibido el mensaje contestamos con el printWritter
                            // En caso de que el resultado sea correcto
                            if (resultado.equals("Correcto")) {
                                out.println("Has ganado,  el número es: " + numero);
                                fin = true;
                            } else if (resultado.equals("JuegoTerminado")) {
                                out.println("El juego ha terminado");
                                fin = true;
                            } else if (resultado.equals("Mayor")) {
                                out.println("El número secreto es mayor. Intentos: " + intentos + "/5");
                            } else if (resultado.equals("Menor")) {
                                out.println("El número secreto es menor. Intentos: " + intentos + "/5");
                            }

                            // En caso de que el número de intentos sea mayor o igual a 5 y no haya terminado ya termina de jugar
                            if (intentos >= 5 && !fin) {
                                out.println("Has agotado los intentos");
                                fin = true;
                            }

                        } catch (NumberFormatException e) {
                            out.println("Error: " + e.getMessage());
                        }
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
}