package Tema1.Tarea4;

import java.io.File;
import java.io.IOException;

public class Ejercicio1 {
    public static void main(String[] args) {
        try {
            traspasarInformacion("C:\\Users\\Alberto.DESKTOP-O1GC77M\\Desktop\\Java\\Java-2526" +
                            "\\ServiciosYProcesos\\src\\Comienzo\\Tarea4\\origen.txt", "C:\\Users" +
                            "\\Alberto.DESKTOP-O1GC77M\\Desktop\\Java\\Java-2526\\ServiciosYProcesos\\src\\Comienzo\\Tarea4\\destino.txt",
                    "C:\\Users\\Alberto.DESKTOP-O1GC77M\\Desktop\\Java\\Java-2526\\ServiciosYProcesos\\src" +
                            "\\Comienzo\\Tarea4\\error.txt");
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Esta función va a filtrar las líneas de un fichero origen a un fichero
     * destino con el regex, en este caso la palabra indicada
     *
     * @param ficheroOrigen  el fichero del que hay que leer
     * @param ficheroDestino el fichero en el que hay que escribir
     * @throws IOException
     * @throws InterruptedException
     */
    public static void traspasarInformacion(String ficheroOrigen, String ficheroDestino, String error) throws IOException, InterruptedException {
        /* Creamos un ProcessBuilder que va a ejecutar el programa independiente findstr.exe (NO ES DEL CMD)
         * por eso no ejecutamos el cmd.exe, le pasamos el parámetro, que es la palabra a buscar en las líneas
         * de un fichero origen, devolverá las coincidencias, no hace falta indicarle el archivo origen porque
         * ya se lo indicamos en la primera tubería */
        // Creamos un error de proceso a propósito pasándole un argumento extra inválido, esto no es válido
        ProcessBuilder pb = new ProcessBuilder("findstr", "fhghf", "problema");
        // Le decimos al proceso lo que recibe, es decir el fichero .txt de donde debe filtrar
        pb.redirectInput(ProcessBuilder.Redirect.from(new File(ficheroOrigen)));
        // Le decimos al proceso donde debe volcar su resultado, en este caso en un fichero .txt
        pb.redirectOutput(ProcessBuilder.Redirect.to(new File(ficheroDestino)));
        // Creamos una tubería que va a escribir los errores que pueden porvocar el porceso en el fichero indicado
        pb.redirectError(ProcessBuilder.Redirect.to(new File(error)));
        // Iniciamos el proceso
        Process process = pb.start();
        process.waitFor();
    }
}
