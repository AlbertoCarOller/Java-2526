package GestorBBDD;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class GestorBBDD {
    private final int longitudMatricula = 7;
    private final int longitudMarca = 32;
    private final int longitudModelo = 32;
    private final String rutaFicheroDat;
    private long totalRegistros = 0;
    private long registrosEnBytes = 0;
    private final String rutaCSV;
    private final String ficheroTemporal = "C:\\Users\\Alberto.DESKTOP-O1GC77M\\Desktop\\Java\\Java-2526\\GestorBDD\\" +
            "src\\main\\java\\GestorBBDD\\baseTemporal.dat";

    // Creamos el constructor
    public GestorBBDD(String rutaFicheroDat, String rutaCSV) throws IOException {
        this.rutaCSV = rutaCSV;
        this.rutaFicheroDat = rutaFicheroDat;
        if (new File(this.rutaFicheroDat).exists()) {
            totalRegistros = Files.size(new File(this.rutaFicheroDat).toPath()) / 71;

        } else {
            Files.createFile(new File(this.rutaFicheroDat).toPath());
        }
    }

    // Creamos los getter
    public int getLongitudMatricula() {
        return longitudMatricula;
    }

    public int getLongitudMarca() {
        return longitudMarca;
    }

    public int getLongitudModelo() {
        return longitudModelo;
    }

    public String getRutaFicheroDat() {
        return rutaFicheroDat;
    }

    public long getTotalRegistros() {
        return totalRegistros;
    }

    /**
     * Esta función va a insertar un registro en la base de datos en caso de que
     * el registro no exista
     *
     * @param matricula matrícula del vehículo
     * @param marca     marca del vehículo
     * @param modelo    modelo del vehículo
     * @throws IOException
     * @throws GestorBBDDException
     */
    public void insertarRegistro(String matricula, String marca, String modelo, long posicion) throws IOException, GestorBBDDException {
        if (posicion > totalRegistros || posicion < 0) {
            throw new GestorBBDDException("No se puede insertar en la posición " + posicion);
        }
        if (matricula.isBlank() || matricula.getBytes().length > longitudMatricula
                || marca.isBlank() || marca.getBytes().length > longitudMarca
                || modelo.isBlank() || modelo.getBytes().length > longitudModelo) {
            throw new GestorBBDDException("Algún campo es inválido");
        }
        if (existe(matricula)) {
            throw new GestorBBDDException("No se permiten registros duplicados");

        } else {
            long posicionEnBytes = posicion * (longitudMatricula + longitudMarca + longitudModelo);
            try (RandomAccessFile rd = new RandomAccessFile(this.rutaFicheroDat, "rw")) {
                List<byte[]> registrosPosteriores = almacenarRegistrosPosteriores(posicionEnBytes, rd);
                // Nos posicionamos donde indica el usuario
                rd.seek(posicionEnBytes);
                /* Escribimos la matrícula formateada con .format(), el %1$- indica que quiere formatear
                 * el primer argumento pasado (sin contar el formateo) y el '-' indica que se deberá de
                 * colocar a la izquierda y a la derecha se rellenará hasta llegar a la longitud pasada
                 * con espacios */
                /* Las codificaciones deben estar en ISO_8859_1 para respetar 1 byte por caracter porque con UTF-8 los
                 caracteres especiales pueden ocupar 2 bytes */
                rd.write(String.format("%1$-" + longitudMatricula + "s", matricula).getBytes(StandardCharsets.ISO_8859_1));
                //escribirEspaciosEnBlanco(longitudMatricula - matricula.getBytes().length, rd);
                rd.write(String.format("%1$-" + longitudMarca + "s", marca).getBytes(StandardCharsets.ISO_8859_1));
                //escribirEspaciosEnBlanco(longitudMarca - marca.getBytes().length, rd);
                rd.write(String.format("%1$-" + longitudModelo + "s", modelo).getBytes(StandardCharsets.ISO_8859_1));
                //escribirEspaciosEnBlanco(longitudModelo - modelo.getBytes().length, rd);
                this.registrosEnBytes += 71; // Sumamos 71 que es lo que ocupa en bytes un registro
                this.totalRegistros += 1; // Sumamos 1 al total de registros
                // Escribimos los registros posteriores
                registrosPosteriores.forEach(r -> {
                    try {
                        rd.write(r);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    /**
     * Esta función va a devolver una lista con cada registro en bytes para
     * posteriormente escribirlos, una vez insertado el nuevo registro
     *
     * @param posicionBytes la posición en bytes que ocupa la posición elegida por el usuario
     * @param rd            RandomAccessFile
     * @return la lista de registros
     * @throws IOException
     */
    private ArrayList<byte[]> almacenarRegistrosPosteriores(long posicionBytes, RandomAccessFile rd) throws IOException {
        rd.seek(posicionBytes);
        // Cada campo de esta lista almacenará un registro
        ArrayList<byte[]> registrosPosteriores = new ArrayList<>();
        // Creamos un array de bytes que va a almacenar un registro
        byte[] registro = new byte[(longitudMatricula + longitudModelo + longitudMarca)];
        while ((rd.read(registro)) != -1) {
            // Se crea una nueva instancia con los valores nuevos
            byte[] registroN = registro.clone();
            registrosPosteriores.add(registroN);
        }
        return registrosPosteriores;
    }

    /**
     * Esta función escribe en la base de datos los espacios en blanco sobrantes de los bytes
     * reservados para cada campo
     *
     * @param espaciosEnBlanco el número de espacios en blanco a escribir
     * @param rd               el RandomAccessFile
     * @throws IOException
     */
    /*private void escribirEspaciosEnBlanco(int espaciosEnBlanco, RandomAccessFile rd) throws IOException {
        for (int i = 0; i < espaciosEnBlanco; i++) {
            rd.writeBytes(" ");
        }
    }*/

    /**
     * Esta función comprueba la existencia de la matrícula pasada en la base de datos,
     * en caso de que exista true y en caso de que no false
     *
     * @param matricula la matrícula a comprobar
     * @return si existe o no ya en la base de datos
     * @throws IOException
     */
    private boolean existe(String matricula) throws IOException {
        // Creamos un RandomAccessFile para poder leer directamente los bytes que contiene la matrícula
        try (RandomAccessFile rd = new RandomAccessFile(this.rutaFicheroDat, "r")) {
            // En el buffer guardamos la matrícula leída
            byte[] bufferMatricula = new byte[longitudMatricula];
            int veces = 0;
            // Mientras no hayamos llegado al final seguimos leyendo
            while ((rd.read(bufferMatricula)) != -1) {
                veces++;
                // Nos adelantamos al siguiente registro
                rd.seek(veces * 71L);

                /* Hay que codificarlo en ISO_8859_1 porque si lo hacemos en UTF-8 puede no escribir los bytes exactos
                 * porque .writeBytes() escribe con codificación ISO_8859_1 ->
                 * String matriculaS = new String(bufferMatricula, StandardCharsets.ISO_8859_1); */

                String matriculaS = new String(bufferMatricula, StandardCharsets.ISO_8859_1);
                if (matriculaS.trim().equals(matricula)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Esta función va a borrar de una lista de registros el registro que contenga la matrícula indicada
     * y va a volcar los datos de la lista con el registro eliminado en un nuevo fichero
     *
     * @param matricula la matrícula del vehículo
     * @throws IOException
     * @throws GestorBBDDException
     */
    public void borrarRegistro(String matricula) throws IOException, GestorBBDDException {
        // Comprobamos si la matrícula existe
        if (!existe(matricula)) {
            throw new GestorBBDDException("La matrícula a borrar no está registrada");
        }
        Path bbddNueva = new File(this.ficheroTemporal).toPath();
        Files.createFile(bbddNueva);

        try (RandomAccessFile leerRegistros = new RandomAccessFile(this.rutaFicheroDat, "r");
             RandomAccessFile escribirRegistros = new RandomAccessFile(this.ficheroTemporal, "rw")) {
            System.out.println("Comenzando el traspaso...");
            // Llamamos a función auxiliar para obtener la lista
            ArrayList<String> registros = pasarRegistrosALista(leerRegistros);
            System.out.println("Registros encontrados");
            registros.removeIf(r -> r.substring(0, longitudMatricula).trim().equals(matricula));
            System.out.println("Registro eliminado");
            escribirRegistros(registros, escribirRegistros, true);
        }
        intercambioFicheros();
        this.totalRegistros -= 1;
        this.registrosEnBytes -= 71;

    }

    /**
     * Esta función va a devolver una lista con los campos de la base de
     * datos pasados a String
     *
     * @param leerRegistros el InputStreamReader
     * @return lista de registros
     * @throws IOException
     */
    private ArrayList<String> pasarRegistrosALista(RandomAccessFile leerRegistros) throws IOException {
        leerRegistros.seek(0);
        ArrayList<String> listaRegistrosALista = new ArrayList<>();
        // Guardamos el registro
        byte[] registro = new byte[longitudMatricula + longitudMarca + longitudModelo];
        // Mientras haya registros lee
        while (leerRegistros.read(registro) != -1) {
            byte[] registroS = registro.clone();
            // Aádimos el registro como cadena a la lista
            listaRegistrosALista.add(new String(registroS, StandardCharsets.ISO_8859_1));
        }
        System.out.println("Registros pasados");
        return listaRegistrosALista;
    }

    /**
     * Esta función va a escribir dentro de la nueva base de datos, todos los registros
     * menos el eliminado
     *
     * @param listaRegistros    la lista de registros a insertar
     * @param escribirRegistros el RandomAccessFile
     * @throws IOException
     * @throws RuntimeException
     */
    private void escribirRegistros(List<String> listaRegistros, RandomAccessFile escribirRegistros, boolean principio)
            throws IOException, RuntimeException {
        if (principio) {
            escribirRegistros.seek(0);
        } else {
            escribirRegistros.seek(this.registrosEnBytes);
        }
        // Escribimos en el nuevo fichero
        listaRegistros.forEach(r -> {
            try {
                escribirRegistros.write(r.getBytes(StandardCharsets.ISO_8859_1));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Esta función va a modificar un registro por la posición creando un archivo temporal
     * con los datos actualizados y renombrándolo con el nombre de la base de datos original
     *
     * @param posicion la posición del registro
     * @param marca    la marca nueva del coche
     * @param modelo   el nuevo modelo del coche
     * @throws GestorBBDDException
     * @throws IOException
     */
    public void modificarRegistro(int posicion, String marca, String modelo) throws GestorBBDDException, IOException {
        if (posicion < 0 || posicion > totalRegistros) {
            throw new GestorBBDDException("La posición no es válida");
        }
        Path bbddNueva = new File(this.ficheroTemporal).toPath();
        Files.createFile(bbddNueva);

        try (RandomAccessFile leer = new RandomAccessFile(this.rutaFicheroDat, "r");
             RandomAccessFile escribir = new RandomAccessFile(this.ficheroTemporal, "rw")) {
            // Obtenemos una lista de los registros
            ArrayList<String> listaRegistros = pasarRegistrosALista(leer);
            // Obtenemos la matrícula
            String matriculaOriginal = listaRegistros.get(posicion).substring(0, longitudMatricula);
            // Creamos el registro modificado
            String registroNuevo = matriculaOriginal.concat(String.format("%1$-" + longitudMarca + "s", marca))
                    .concat(String.format("%1$-" + longitudModelo + "s", modelo));
            // Reemplazamos el registro antiguo por el nuevo
            listaRegistros.set(posicion, registroNuevo);
            // Escribimos los registros actualizados en el fichero temporal
            escribirRegistros(listaRegistros, escribir, true);
        }
        intercambioFicheros();
    }

    /**
     * Esta función va a ordenar alfabéticamente los registros por la matrícula
     * de forma descendente
     *
     * @throws GestorBBDDException
     * @throws IOException
     */
    public void ordenarPorMatricula() throws GestorBBDDException, IOException {
        if (this.totalRegistros == 0) {
            throw new GestorBBDDException("No hay registros para ordenar");
        }

        try (RandomAccessFile leer = new RandomAccessFile(this.rutaFicheroDat, "r");
             RandomAccessFile escribir = new RandomAccessFile(this.ficheroTemporal, "rw")) {
            // Obtenemos la lista de los registros
            ArrayList<String> listaRegistros = pasarRegistrosALista(leer);
            // Ordenamos la lista alfabéticamente de manera descendente
            ArrayList<String> listaRegistrosOrdenada = new ArrayList<>(listaRegistros.stream().sorted().toList().reversed());
            // Escribimos los registros en el fichero temporal
            escribirRegistros(listaRegistrosOrdenada, escribir, true);
        }
        intercambioFicheros();
    }

    public void cargarCSV() throws GestorBBDDException, IOException {
        File csv = new File(this.rutaCSV);
        if (!csv.exists() || csv.length() == 0) {
            throw new GestorBBDDException("No se ha encontrado el CSV");
        }
        try (BufferedReader leer = new BufferedReader(new FileReader(this.rutaCSV));
             RandomAccessFile escribir = new RandomAccessFile(this.rutaFicheroDat, "rw")) {
            // Obtenemos una lista con los registros formateados del CSV
            ArrayList<String> registrosFormateadosCSV = pasarCSVALista(leer);
            // Escribimos en la base de datos
            escribirRegistros(registrosFormateadosCSV, escribir, false);
        }
    }

    /**
     * Esta función va a devolver una lista con los registros del fichero
     * CSV
     *
     * @param leer el BufferedReader
     * @return la lista con los registros del CSV
     * @throws IOException
     * @throws GestorBBDDException
     */
    private ArrayList<String> pasarCSVALista(BufferedReader leer) throws IOException, GestorBBDDException {
        // Creamos la lista que va a almacenar los registros
        ArrayList<String> listaRegistros = new ArrayList<>();
        String linea;
        // Creamos un contador
        int contador = 0;
        while ((linea = leer.readLine()) != null) {
            // Si el contador es distinto de 0 formatea y añade, si no, no, ya que la primera línea no es un registro
            if (contador != 0) {
                // Separamos los campos de cada registro
                String[] campos = linea.split(",");
                // Comprobamos si están los 3 campos
                if (campos.length != 3) {
                    throw new GestorBBDDException("Los registros no están bien formados");
                }
                String registroFormateado = "";
                // Formateamos cada campo
                registroFormateado = registroFormateado.concat(String.format("%1$-" + longitudMatricula + "s", campos[0].trim()));
                registroFormateado = registroFormateado.concat(String.format("%1$-" + longitudMarca + "s", campos[1].trim()));
                registroFormateado = registroFormateado.concat(String.format("%1$-" + longitudModelo + "s", campos[2].trim()));
                listaRegistros.add(registroFormateado);
            }
            contador++;
        }
        return listaRegistros;
    }

    /**
     * Esta función se encarga de reajustar los ficheros y nombres cuando creo
     * ficheros temporales para almacenar la información actualizada
     *
     * @throws IOException
     */
    private void intercambioFicheros() throws IOException {
        // Eliminamos la base de datos vieja en caso de que exista
        Files.deleteIfExists(Path.of(this.rutaFicheroDat));
        /* Cambiamos el nombre del fichero temporal al nombre original que debe de tener y pasamos su contenido
         * de eso se trata .move(archivoViejo, archivoNuevo) */
        Files.move(new File(this.ficheroTemporal).toPath(), Path.of(this.rutaFicheroDat), StandardCopyOption.REPLACE_EXISTING);
        // Borramos la base de datos temporal una vez pasado el contenido
        Files.deleteIfExists(Path.of(this.ficheroTemporal));
    }
}