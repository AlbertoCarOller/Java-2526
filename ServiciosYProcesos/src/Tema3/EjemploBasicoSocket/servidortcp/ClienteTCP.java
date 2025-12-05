package Tema3.EjemploBasicoSocket.servidortcp;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClienteTCP {
    static Socket cliente;
    static ObjectOutputStream salida;
    static ObjectInputStream entrada;
    static int puerto = 5001;
    public static void main(String args[]) {
        try {
            cliente = new Socket("localhost", puerto); //Creamos el socket y llamamos al server
            salida = new ObjectOutputStream(cliente.getOutputStream());
            entrada = new ObjectInputStream(cliente.getInputStream()); //Creamos canales de entrada/salida
            System.out.println("Canales cliente establecidos");
            conversacion();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            ;
        } finally {
            try {
                cliente.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // =================================== ESTE ES EL UNICO MÉTODO DONDE HAY QUE MODIFICAR CODIGO =================
    public static void conversacion() {
        try {
            String mensaje = "Pepe Porretas";
            salida.writeObject(mensaje);
            String respuesta = (String) entrada.readObject();
            System.out.println("Mi mensaje es: Buenas, soy " + mensaje);
            System.out.println("Respuesta del servidor: " + respuesta);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
