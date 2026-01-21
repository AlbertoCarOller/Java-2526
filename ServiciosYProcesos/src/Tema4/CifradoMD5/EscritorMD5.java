package Tema4.CifradoMD5;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

public class EscritorMD5 {

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

    /**
     * Esta función va a escribir en un txt la contraseña cifrada en hexadecimal
     *
     * @param password la contraseña cifrada a escribir
     * @throws IOException en caso de cualquier error
     */
    public static void writeHashToFile(String password) throws IOException {
        // Creamos un BufferedWritter para escribir en el fichero el password pasado a hash
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/Tema4/CifradoMD5/cifredPassword.txt"))) {
            // Escribimos con write() la contraseña cifrada
            bw.write(password);
        }
    }
}