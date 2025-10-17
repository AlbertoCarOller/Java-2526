package Tema1;

import java.io.IOException;

public class Ejemplo1 {
    public static void main(String[] args) {
        try {
            // Creamos un objeto del ProcessBuilder que nos va a permitir crear un proceso, en este caso abrir la calculadora
            ProcessBuilder pb = new ProcessBuilder("calc".toUpperCase());
            // Ejecutamos el proceso
            Process p = pb.start();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}