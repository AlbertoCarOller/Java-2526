package EjerciciosExtras;

import utils.MiEntradaSalida;

public class NumRomanos {
    public static void main(String[] args) {
        System.out.println("El siglo es: " + calcularSiglo(MiEntradaSalida.solicitarEntero("Introduce el año")));
    }

    public static int calcularSiglo(int ano) {
        if (ano < 1) {
            return 0;
        }
        int siglo = 1;
        while (ano > 100) {
            ano -= 100;
            siglo++;
        }
        return siglo;
    }
}