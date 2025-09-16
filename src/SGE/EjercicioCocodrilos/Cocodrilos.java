package SGE.EjercicioCocodrilos;

import utils.MiEntradaSalida;

import java.util.HashSet;
import java.util.Set;

public class Cocodrilos {
    public static void main(String[] args) {
        int numCocod = 0;
        while (numCocod < 1 || numCocod > 50) {
            numCocod = MiEntradaSalida.solicitarEnteroPositivo("Introduce el número");
        }
        // Solicitamos los nombres
        int contador = 0;
        int contadorLlorores = 0;
        Set<String> letras = new HashSet<>();
        while (contador < numCocod) {
            letras.clear();
            contador++;
            String nombreCartel = MiEntradaSalida.solicitarCadena("Introduce el nombre del cartel");

            // Comprobamos que haya dos vocales diferentes
            for (int i = 0; i < nombreCartel.length(); i++) {
                char letra = nombreCartel.charAt(i);
                if (isVocal(letra)) {
                    letras.add(String.valueOf(letra));
                }
            }

            if (letras.size() == 2) {
                contadorLlorores++;
            }

        }
        System.out.println("Hay " + contadorLlorores + " llorones");
    }

    public static boolean isVocal(char letra) {
        if (letra == 'a' || letra == 'e' || letra == 'i' || letra == 'o' || letra == 'u'
        || letra == 'A' || letra == 'E' || letra == 'I' || letra == 'O' || letra == 'U'
        || letra == 'á' || letra == 'é' || letra == 'í' || letra == 'ó' || letra == 'ú') {
            return true;
        }
        return false;
    }
}
