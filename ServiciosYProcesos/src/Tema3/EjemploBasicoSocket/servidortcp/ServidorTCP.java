package Tema3.EjemploBasicoSocket.servidortcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorTCP {
    static ServerSocket servidor;
    static Socket conexion;
    static ObjectOutputStream salida;
    static ObjectInputStream entrada;
    static int puerto = 5001;
    public static void main(String args[]) {
        try {
            servidor = new ServerSocket(puerto); // Creamos un ServerSocket en el puerto 5000
            System.out.println("Servidor Arrancado.");
            System.out.println("Esperando llamada de conexion de algun cliente...");
            conexion = servidor.accept(); // Esperamos una conexión
            System.out.println("Conectado en : " + conexion.getInetAddress().getHostName());
            entrada = new ObjectInputStream(conexion.getInputStream()); // Abrimos los canales de E/S
            salida = new ObjectOutputStream(conexion.getOutputStream());
            System.out.println("Canales con el cliente establecidos");
            conversacion();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                conexion.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // =================================== ESTE ES EL UNICO MÉTODO DONDE HAY QUE MODIFICAR CODIGO =================
    public static void conversacion() {
        try {
            String mensaje = (String) entrada.readObject();
            System.out.println("Mensaje recibido: " + mensaje);
            salida.writeObject("Buenos dias " + mensaje); // Le respondemos
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}