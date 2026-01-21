package Tema4.CifradoMD5;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Scanner;

public class LectorMD5 {
    static void main(String[] args) {
        // Creamos el Scanner
        try (Scanner sc = new Scanner(System.in)) {
            // Creamos la liquadora para cifrar el mensaje a 'MD5'
            MessageDigest md = MessageDigest.getInstance("MD5");
            // Solicitamos al usuario la contraseña a leer
            System.out.println("Introduce la contraseña a escribir en el fichero");
            // Ciframos la contraseña y la escribimos en el txt
            String passwordHexOrig = String.valueOf(EscritorMD5.transformToHash(md, sc.nextLine()));
            EscritorMD5.writeHashToFile(passwordHexOrig);
            // Una vez hecho esto, volvemos a solicitar al usuario la contraseña
            System.out.println("Introduce la misma contraseña, para leerla del fichero y comparala");
            // Comprabamos si son iguales
            boolean iguales = isSameHex(readHashFromFile(),
                    String.valueOf(EscritorMD5.transformToHash(md, sc.nextLine())));

            // Comprobamso si los hash son iguales
            if (iguales) {
                System.out.println("Los hash son iguales, el hash en cuestión: " + passwordHexOrig);
            } else {
                System.out.println("Los hash no coinciden");
            }

        } catch (GeneralSecurityException | IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Esta función va a leer el hash de un txt predeterminado
     *
     * @return devuelve el hash leído
     * @throws IOException en caso de algún error
     */
    public static String readHashFromFile() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("src/Tema4/CifradoMD5/cifredPassword.txt"))) {
            // Devolvemos la línea leída, ahí debe estar el hash
            return br.readLine();
        }
    }

    /**
     * Esta función va a comparar dos cadenas
     *
     * @param s1 la primera cadena
     * @param s2 la segunda cadena
     * @return true o false dependiendo de si son iguales o no
     */
    public static boolean isSameHex(String s1, String s2) {
        // Devolvemos true o false dependiendo de si son iguales o no
        return s1.equals(s2);
    }
}
