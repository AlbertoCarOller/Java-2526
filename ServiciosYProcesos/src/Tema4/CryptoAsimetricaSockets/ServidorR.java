package Tema4.CryptoAsimetricaSockets;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;

public class ServidorR {
    static void main(String[] args) {
        // Creamos un Socket que escuche en el puerto 5000
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            // Mostramos un mensaje de inicialización de escucha del puerto
            System.out.println("Servidor escuchando en el puerto " + serverSocket.getLocalPort());
            /* Creamos el KeyPairGenerator, este crea dos llaves, es la fábrica, una pública para el usuario y
             otra privada del servidor, con el formato de Encriptado RSA */
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            // Con el initialize le decimos a la cerradura como de fuerte debe de ser, cuanto más bytes, más fuerte
            keyPairGenerator.initialize(2048);
            // Creamos el par de llaves
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            // Almacenamos la llave pública y privada
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            System.out.println("Esperando a cliente");
            // Se acepta a un cliente
            Socket socket = serverSocket.accept();
            // Creamos los canales de comunicación
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            // Ahora creamos el array de bytes que representa la clase pública
            byte[] keyBytes = publicKey.getEncoded();
            System.out.println("Enviando clave pública de " + keyBytes.length + " bytes");
            // Enviamos primero la longitud para que el cliente sepa la longitud de bytes de la clave
            dataOutputStream.writeInt(keyBytes.length);
            // Enviamos los bytes (la clave pública)
            dataOutputStream.write(keyBytes);
            // Recibimos la longitud del mensaje
            int longitudMensaje = dataInputStream.readInt();
            // Creamos un array de bytes con el tamaño del mensaje
            byte[] mensaje = new byte[longitudMensaje];
            // Recibimos el mensaje encriptado (array de bytes), leemos todos los bytes
            dataInputStream.readFully(mensaje);
            // Desencriptamos el mensaje
            Cipher cipher = Cipher.getInstance("RSA");
            // Lo ponemos en modo desencriptación y la desbloqueamos con la llave privada
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            // Guardamos el mensaje desemcriptado
            byte[] mensajeDescriptado = cipher.doFinal(mensaje);
            // Mostramos el mensaje recibido
            System.out.println("Mensaje recibido: " + new String(mensajeDescriptado));
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
