package Tema1.Tarea1;

import java.io.File;
import java.io.IOException;

public class ProcesoSimple {
    public static void main(String[] args) throws IOException {
        // -- Encierro toda la logica con un try-catch y controlo los errores
        try {
            // -- Creo un proceso nuevo, en dos pasos:
            // 1.- Creo un "ConstructorDeProcesos" (ProcessBuilder) con los argumentos del proceso
            // que quiero crear, que se indican godos seguidos en el constructor de ProcessBuilder
            // Se prtende hacer un ls -l ( solo vale en un entorno Unix u OSx)
            //ProcessBuilder obProBuil = new ProcessBuilder("ls", "-l");
            ProcessBuilder obProBuil = new ProcessBuilder("cmd.exe", "/C", "java -jar C:\\Users\\Alberto" +
                    ".DESKTOP-O1GC77M\\Desktop\\Java\\Java-2526\\ServiciosYProcesos\\src\\Comienzo\\Tarea1\\Contador10R.jar");
            // indicamos donde ha de buscar la instruccion del proceso a realizar. Si fuera una clase compilada en el mismo
            // proyecto java actual, estaría en "./bin". Como es una instruccion del SO; la encontrara por la variable PATH
            obProBuil.directory(new File("."));
            obProBuil.inheritIO();
            // 2.- Creo el proceso, arrancando el constructor de procesos
            Process obProcesoHijo = obProBuil.start();
            // waitFor() obliga a que nuestro programa quede esperando a que el proceso obProcesoHijo acabe,
            // y cuando acaba, waitFor() devuelve el codigo exit que emite el proceso
            // Normalmente, se devuelve un 0 si la ejecucion del proceso ha sido correcta.
            int codExit = obProcesoHijo.waitFor();
            // -- Escribimos el resultado de la ejecucion.
            // En este caso, lo que escrib es : "La ejecución de [ls, -l] devuelve 0"
            System.out.println("La ejecución de 'cmd.exe /C dir'  devuelve: " + codExit);
        } catch (IOException ex) {
            System.err.println("Excepción de E/S!!");
            ex.printStackTrace();
            // -- Con esta instruccion hacemos que termine nuestro programa (que es tb un proceso)
            // e indicamos el código de salida que damos al acabar.
            // Es el mismo código que antes veiamos que devolvia el método waitFor(), solo que aqui
            // vemos como se provoca un código de salida específico.
            // Si este proceso nuestro hubiera sido llamado por otro, podría ver que con un -1
            // de valor de salida, habia habido un error.
            System.exit(-1);
        } catch (InterruptedException ex) {
            System.err.println("El proceso hijo finalizo de forma incorrecta");
            System.exit(-2);
        }
        System.exit(0);
    }


}