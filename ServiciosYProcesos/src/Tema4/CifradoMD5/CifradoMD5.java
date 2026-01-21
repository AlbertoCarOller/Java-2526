package Tema4.CifradoMD5;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Scanner;

public class CifradoMD5 {
    static void main(String[] args) {
        // Creamos el Scanner para leer por teclado
        try (Scanner sc = new Scanner(System.in)) {
            // Esta clase convierte los String en un String fijo (el hash), pasándole el algoritmo 'MD5'
            MessageDigest md = MessageDigest.getInstance("MD5");
            // Solicitamos la contraseña
            System.out.println("Introduce la contraseña");
            // Almacenamos el password
            String password = sc.nextLine();
            String cifradoHex = String.valueOf(transformToHash(md, password));
            // Imprimimos por pantalla el hash
            System.out.println("Contraseña pasada por primera vez: " + cifradoHex);
            // Solicitamos la segunda contraseña
            System.out.println("Introduce la contraseña");
            // Almacenamos el segundo password
            password = sc.nextLine();
            // Le pasamos el segundo password
            cifradoHex = String.valueOf(transformToHash(md, password));
            // Mostramos por pantalla el segundo
            System.out.println("Contraseña pasada por segunda vez: " + cifradoHex);
        } catch (GeneralSecurityException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Esta función va a cifrar a 'MD5' un mensaje pasado por
     * el usuario (una contraseña)
     *
     * @param md       el MessageDigest para transformar a 'MD5'
     * @param password la contraseña introducida por el usuario
     * @return la contraseña transformada a hexadecimal tras pasar a 'MD5'
     * @throws GeneralSecurityException en caso de cualquier error la excepción más general
     *                                  cuando se maneja con estas clases
     */
    public static StringBuilder transformToHash(MessageDigest md, String password)
            throws GeneralSecurityException {
        // Liquiamos el password para convertirlo en un hash, con la función digest(), solo acepta array de bytes
        byte[] hash = md.digest(password.getBytes());
            /* Bien, como ahora no es legible lo que debemos de hacer es transformarlo a hexadecimal para
             que sí pueda ser legible, hacemos un bucle for, guardando en un StringBuilder la conversión */
        StringBuilder cifradoHex = new StringBuilder();
        for (Byte b : hash) {
            // Ciframos a hexadecimal
            cifradoHex.append(String.format("%02x", b));
        }
        // Devolvemos el cifrado hexadecimal
        return cifradoHex;
    }
}
