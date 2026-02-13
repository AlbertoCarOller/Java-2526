package Tema3.EjercicioAdivinanzaMultichat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ServidorChat {
    // Creamos el puerto del servidor, en este caso nos inventamos este número
    public static int puertoServidor = 8888;
    public static Scanner sc = new Scanner(System.in);
    // Creamos una lista estática que va a guardar los hilos para después difundir sus mensajes
    public static List<HiloDelegadorServidor> listaHilos = new ArrayList<>();

    public static void main(String[] args) {
        /* Vamos a tener que envolverlo (ServerSocket) en un try, es autocloseable,
         * este objeto es como el portero de un hotel, es el encargado de atender llamadas, no hace nada más */
        // Metemos el server en un try y le pasamos el puerto, aquí es cuando realmente se crear el server
        try (ServerSocket ss = new ServerSocket(puertoServidor)) {
            // Creamos una llamada (Socket), esperando a ser una llamada real
            Socket socket;

            // --- CAMBIO: Creamos la partida compartida una sola vez ---
            Partida partida = new Partida();

            // Mostramos un mensaje de que el servidor se ha iniciado y está esperando a un usuario
            System.out.println("Iniciando servidor...esperando una llamada...");
            // Siempre nos quedamos esperando a que haya una llamada, cuando haya una, esta se acepta
            while (true) {
                // Aceeptamos la llamada y se queda el código bloqueado hasta que reciba una llamada
                socket = ss.accept();
                /* Mensaje que indica que se ha aceptado una llamada,
                 getInetAddress() -> devuelve la dirección IP
                 getHostName() -> intenta traducir la dirección IP a un nombre legible como localhost...*/
                System.out.println(socket.getInetAddress().getHostName() + " ha sido aceptada");

                // Creamos el delegador del servidor que va a leer y escribir
                HiloDelegadorServidor hilo = new HiloDelegadorServidor(socket, partida);

                // Lo guardamos en la lista
                listaHilos.add(hilo);
                hilo.start();
            }

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}