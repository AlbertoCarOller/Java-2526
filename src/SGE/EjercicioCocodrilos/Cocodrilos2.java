package SGE.EjercicioCocodrilos;

import utils.MiEntradaSalida;

public class Cocodrilos2 {
    public static void main(String[] args) {
        int numCocod = MiEntradaSalida.solicitarEnteroEnRango("Introduce el número de cocodrilos", 1, 50);
        System.out.println("Hay " + recorrerNombres(numCocod) + " llorones");
    }

    public static int recorrerNombres(int numCocod) {
        int contadorLloron = 0;
        int contador = 0;
        while (numCocod > contador) {
            contador++;
            if (isLloron(MiEntradaSalida.solicitarCadena("Introduce el nombre"))) {
                contadorLloron++;
            }
        }
        return contadorLloron;
    }

    public static boolean isLloron(String nombre) {
        int contadorVocales = 0;
        nombre = nombre.toLowerCase();
        if (nombre.contains("a") || nombre.contains("á")) {
            contadorVocales++;
        }

        if (nombre.contains("e") || nombre.contains("é")) {
            contadorVocales++;
        }

        if (nombre.contains("i") || nombre.contains("í")) {
            contadorVocales++;
        }

        if (nombre.contains("o") || nombre.contains("ó")) {
            contadorVocales++;
        }

        if (nombre.contains("u") || nombre.contains("ú")) {
            contadorVocales++;
        }
        if (contadorVocales == 2) {
            return true;
        }
        return false;
    }
}