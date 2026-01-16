package Tema4.EjercicioCriptografia;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;

public class main {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            /* KeyGenerator -> Esta clase es un generador de claves encriptadas,
             * en su función getInstance() por parámetros ponemos el tipo de encriptación
             * que utiliza */
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            /* Hemos generado una clave segura con la función generateKey() de la fábrica KeyGenerator,
             * el objeto llave es SecretKey, utilizará el cifrado 'AES' */
            SecretKey secretKey = keyGenerator.generateKey();
            /* Creamos la clase Cipher que es la encargada de encriptar la llave SecretKey,
             * obtenemos una instancia de Cipher con el tipo de cifrado para encriptar,
             * como estamos con encriptación simétrica la clave es compartida */
            Cipher cipher = Cipher.getInstance("AES");
            // Llamamos a las funciones para de encriptación y desencriptación
            System.out.println("Introduce el mensaje a encriptar: ");
            String mensajeEncriptado = encriptarMensaje(secretKey, cipher, sc.nextLine());
            System.out.println("Mensaje encriptado: " + mensajeEncriptado);
            String mensajeDesencriptado = desencriptarMensaje(mensajeEncriptado, secretKey, cipher);
            System.out.println("Mensaje desencriptado: " + mensajeDesencriptado);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Esta función va a desencriptar el mensaje antes encriptado en el main
     *
     * @param mensajeCodificado el mensaje codificado
     * @param secretKey         la clave secreta
     * @param cipher            el cipher para descodificar
     * @return el String descodificado
     * @throws InvalidKeyException       en caso de error en su campo
     * @throws IllegalBlockSizeException en caso de error en su campo
     * @throws BadPaddingException       en caso de error en su campo
     */
    public static String desencriptarMensaje(String mensajeCodificado, SecretKey secretKey, Cipher cipher)
            throws InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException {
        // Desencriptamos
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        // Lo convertimos a un array de bytes para descodificarlo de base 64, obtenemos los bytes crudos
        byte[] mensajeByte = Base64.getDecoder().decode(mensajeCodificado); // En este punto tenemos los bytes crudos
        // Lo descodificamos finalmente a bytes no crudos
        byte[] mensajeDesencriptado = cipher.doFinal(mensajeByte); // doFinal() desencripta los bytes para que sean legibles por el String
        // Como cipher.doFinal(mensajeByte) -> devuelve un array de bytes no en crudo
        return new String(mensajeDesencriptado); // Los String son capaces codificar los bytes en Strings
    }

    /**
     * Esta función va a devolver el mensaje pasado por parámetros de forma
     * encriptada
     *
     * @param secretKey la clave secreta
     * @param cipher    el codificador
     * @param mensaje   el mensaje a encriptar
     * @return el String con el mensaje encriptado
     * @throws InvalidKeyException       en caso de error en su campo
     * @throws IllegalBlockSizeException en caso de error en su campo
     * @throws BadPaddingException       en caso de error en su campo
     */
    public static String encriptarMensaje(SecretKey secretKey, Cipher cipher, String mensaje) throws InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        /* Una vez que tenemos el Cipher (encriptador) utilizamos la función
         * init() para encriptarla, pasándole ENCRYPT_MODE para que la encripte,
         * y le pasamos la llave a encriptar, es decir la SecretKey, cipher
         * solo entiende de bytes */
        cipher.init(Cipher.ENCRYPT_MODE, secretKey); // Al encriptar es ilegible por el humano
        // La función doFinal() encripta finalmente el mensaje, devolviendo un array de bytes crudos
        byte[] mensajeCifrado = cipher.doFinal(mensaje.getBytes()); // Paso final de encriptado
        /* Mostramos el mensaje, pero lo codificamos en base 64 porque si esto no se hiciera aparece simbología
         * ilegible de los bytes crudos, con esto sería legible */
        return Base64.getEncoder().encodeToString(mensajeCifrado); // Devuelve letras legibles por el humano
    }
}
