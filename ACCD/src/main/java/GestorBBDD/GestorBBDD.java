package GestorBBDD;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GestorBBDD {
    private final int longitudMatricula = 7;
    private final int longitudMarca = 32;
    private final int longitudModelo = 32;
    private final String rutaFicheroDat; // -> La ruta donde se encuentra la bbdd
    private long totalRegistros = 0;
    private long registrosEnBytes = 0; // -> Los registros que hay en bytes
    private final String rutaCSV; // -> La ruta donde se encuentra el CSV
    private final String ficheroTemporal = "C:\\Users\\Alberto.DESKTOP-O1GC77M\\Desktop\\Java\\Java-2526\\GestorBDD\\" +
            "src\\main\\java\\GestorBBDD\\baseTemporal.dat"; /* -> La ruta del fichero temporal que almacena los cambios
             realizados en el fichero bbdd original, al cual (temporal) posteriormente se le cambia el nombre por el
              nombre de la ruta original */

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
    public String insertarRegistro(String matricula, String marca, String modelo, long posicion) throws IOException, GestorBBDDException {
        // Comprobamos que la posición sea correcta
        validarPosicion(posicion);
        // Comprobamos que los campos sean válidos (tamaño, y que no estén en blanco)
        if (problemaCampos(matricula, marca, modelo)) {
            throw new GestorBBDDException("Algún campo es inválido");
        }
        // Comprobamos que la matrícula no esté ya en la bbdd
        if (existe(matricula) != -1) {
            throw new GestorBBDDException("No se permiten registros duplicados");

            // En caso de que no exista la matrícula, insertamos
        }
        // Calculamos la posición en bytes para insertar
        long posicionEnBytes = posicion * (longitudMatricula + longitudMarca + longitudModelo);
        try (RandomAccessFile rd = new RandomAccessFile(this.rutaFicheroDat, "rw")) {
                /* Desde 'posicionEnBytes' hasta el final del fichero guarda el contenido para insertarlo
                 después de insertar el nuevo registro */
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
        return "Se ha insertado el registro " + matricula + "-" + marca + "-" + modelo + " en la posicion " + posicion;
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
     * Esta función comprueba la existencia de la matrícula pasada en la base de datos
     * y devuelve la posición
     *
     * @param matricula la matrícula a comprobar
     * @return la posición donde se encuentra la matrícula
     * @throws IOException
     */
    private long existe(String matricula) throws IOException {
        // -1 no existe, cualquier otro sí existe
        long posicion = -1;
        // Creamos un RandomAccessFile para poder leer directamente los bytes que contiene la matrícula
        try (RandomAccessFile rd = new RandomAccessFile(this.rutaFicheroDat, "r")) {
            // En el buffer guardamos la matrícula leída
            byte[] bufferMatricula = new byte[longitudMatricula];
            int veces = 0;
            // Mientras no hayamos llegado al final seguimos leyendo
            while ((rd.read(bufferMatricula)) != -1) {
                posicion++; // -> Esto nos indicará la posición en la se encuentra el registro
                veces++; // -> Esto nos servirá para calcular donde está el próximo registro, si es que hay
                // Nos adelantamos al siguiente registro
                rd.seek(veces * 71L);

                /* Hay que codificarlo en ISO_8859_1 porque si lo hacemos en UTF-8 puede no escribir los bytes exactos
                 * porque .writeBytes() escribe con codificación ISO_8859_1 ->
                 * String matriculaS = new String(bufferMatricula, StandardCharsets.ISO_8859_1); */

                String matriculaS = new String(bufferMatricula, StandardCharsets.ISO_8859_1);
                if (matriculaS.trim().equals(matricula)) {
                    return posicion;
                }
            }
        }
        return -1;
    }

    /**
     * Esta función va a borrar de una lista de registros el registro que contenga la matrícula indicada
     * y va a volcar los datos de la lista con el registro eliminado en un nuevo fichero
     *
     * @param matricula la matrícula del vehículo
     * @throws IOException
     * @throws GestorBBDDException
     */
    public String borrarRegistroMatricula(String matricula) throws IOException, GestorBBDDException {
        // Comprobamos si la matrícula existe y obtenemos su posición
        long posicion = existe(matricula);
        if (posicion == -1) {
            throw new GestorBBDDException("La matrícula a borrar no está registrada");
        }
        // Creamos el path del nuevo fichero
        Path bbddNueva = new File(this.ficheroTemporal).toPath();
        // Creamos el fichero nuevo
        Files.createFile(bbddNueva);

        try (RandomAccessFile leerRegistros = new RandomAccessFile(this.rutaFicheroDat, "r");
             RandomAccessFile escribirRegistros = new RandomAccessFile(this.ficheroTemporal, "rw")) {
            // Llamamos a función auxiliar para obtener la lista de registros
            ArrayList<String> registros = pasarRegistrosALista(leerRegistros);
            // Eliminamos el registro de la lista
            registros.remove((int) posicion);
            // Escribimos los registros de la lista
            escribirRegistros(registros, escribirRegistros, 0);
        }
        // Hacemos el cambio de ficheros
        intercambioFicheros();
        this.totalRegistros -= 1;
        this.registrosEnBytes -= 71;
        return "Se ha borrado el registro en la posición " + posicion;

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
    private void escribirRegistros(List<String> listaRegistros, RandomAccessFile escribirRegistros, long posicionInicial)
            throws IOException, RuntimeException {
        escribirRegistros.seek(posicionInicial);
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
    public String modificarRegistro(long posicion, String marca, String modelo) throws GestorBBDDException, IOException {
        validarPosicion(posicion);
        Path bbddNueva = new File(this.ficheroTemporal).toPath();
        Files.createFile(bbddNueva);

        try (RandomAccessFile leer = new RandomAccessFile(this.rutaFicheroDat, "r");
             RandomAccessFile escribir = new RandomAccessFile(this.ficheroTemporal, "rw")) {
            // Obtenemos una lista de los registros
            ArrayList<String> listaRegistros = pasarRegistrosALista(leer);
            // Obtenemos la matrícula
            String matriculaOriginal = listaRegistros.get((int) posicion).substring(0, longitudMatricula);
            // Creamos el registro modificado
            String registroNuevo = matriculaOriginal.concat(String.format("%1$-" + longitudMarca + "s", marca))
                    .concat(String.format("%1$-" + longitudModelo + "s", modelo));
            // Reemplazamos el registro antiguo por el nuevo
            listaRegistros.set((int) posicion, registroNuevo);
            // Escribimos los registros actualizados en el fichero temporal
            escribirRegistros(listaRegistros, escribir, 0);
        }
        intercambioFicheros();
        return "Se ha modificado el registro " + posicion + " -> Marca y modelo actualizados";
    }

    /**
     * Esta función va a eliminar el registro por la posición indicada
     *
     * @param posicion la posición del registro a borrar
     * @throws GestorBBDDException
     * @throws IOException
     */
    public String borrarRegistroPorPosicion(long posicion) throws GestorBBDDException, IOException {
        validarPosicion(posicion);
        Path bbddNueva = new File(this.ficheroTemporal).toPath();
        Files.createFile(bbddNueva);
        try (RandomAccessFile leer = new RandomAccessFile(this.rutaFicheroDat, "r");
             RandomAccessFile escribir = new RandomAccessFile(this.ficheroTemporal, "rw")) {
            // Obtenemos una lista de los registros
            ArrayList<String> listaRegistros = pasarRegistrosALista(leer);
            // Eliminamos el registro en la posición indicada por el usuario
            listaRegistros.remove((int) posicion);
            // Escribimos los registros actualizados en el fichero temporal
            escribirRegistros(listaRegistros, escribir, 0);
        }
        intercambioFicheros();
        this.totalRegistros -= 1;
        this.registrosEnBytes -= 71;
        return "Se ha borrado el registro en la posición " + posicion;
    }

    /**
     * Esta función va a ordenar alfabéticamente los registros por la matrícula
     * de forma descendente
     *
     * @throws GestorBBDDException
     * @throws IOException
     */
    public String ordenarPorMatricula() throws GestorBBDDException, IOException {
        if (this.totalRegistros == 0) {
            throw new GestorBBDDException("No hay registros para ordenar");
        }

        try (RandomAccessFile leer = new RandomAccessFile(this.rutaFicheroDat, "r");
             RandomAccessFile escribir = new RandomAccessFile(this.ficheroTemporal, "rw")) {
            // Obtenemos la lista de los registros
            ArrayList<String> listaRegistros = pasarRegistrosALista(leer);
            // Ordenamos la lista alfabéticamente de manera descendente
            ArrayList<String> listaRegistrosOrdenada = new ArrayList<>(listaRegistros.stream()
                    /* String.CASE_INSENSITIVE_ORDER -> Ordena sin tener en cuenta las mayúsculas y minúsculas,
                     porque devuelve un Comparator<String> */
                    .sorted(String.CASE_INSENSITIVE_ORDER).toList().reversed());
            // Comparamos por el campo marca para que un case insensitive utlizamos el '.compareToIgnoreCase()'
            /*ArrayList<String> listaRegistrosOrdenada = new ArrayList<>(listaRegistros.stream()
                    .sorted((b1, b2) -> b1.substring(longitudMatricula, longitudMarca)
                            .compareToIgnoreCase(b2.substring(longitudMatricula, longitudMarca))).toList().reversed());*/
            // Escribimos los registros en el fichero temporal
            escribirRegistros(listaRegistrosOrdenada, escribir, 0);
        }
        intercambioFicheros();
        return "Se han ordenado los registros por matrícula";
    }

    /**
     * Esta función carga el contenido del CSV al fichero que
     * simula la bbdd, insertará los registros donde le diga
     * el usuario desplazando así los registros de la bbdd
     *
     * @param posicion la posición donde se va a insertar el CSV
     * @return
     * @throws GestorBBDDException
     * @throws IOException
     */
    public String cargarCSV(long posicion) throws GestorBBDDException, IOException {
        validarPosicion(posicion);
        File csv = new File(this.rutaCSV);
        if (!csv.exists() || csv.length() == 0) {
            throw new GestorBBDDException("No se ha encontrado el CSV");
        }
        try (BufferedReader leer = new BufferedReader(new FileReader(this.rutaCSV));
             RandomAccessFile escribir = new RandomAccessFile(this.rutaFicheroDat, "rw")) {
            // Almacenamos los registros posteriores
            ArrayList<byte[]> registrosAlmacenados = almacenarRegistrosPosteriores(posicion * 71, escribir);
            // Obtenemos una lista con los registros formateados del CSV
            ArrayList<String> registrosFormateadosCSV = pasarCSVALista(leer);
            // Escribimos en la base de datos
            escribirRegistros(registrosFormateadosCSV, escribir, (posicion * 71));
            // Escribimos los registros almacenados
            registrosAlmacenados.forEach(c -> {
                try {
                    escribir.write(c);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            // Actualizamos las varibles
            this.totalRegistros += registrosFormateadosCSV.size();
            this.registrosEnBytes += (71L * registrosFormateadosCSV.size());
            return "Se han podido cargar " + registrosFormateadosCSV.size() + " registros";
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
                String registroFormateado;
                // Formateamos cada campo
                String matricula = String.format("%1$-" + longitudMatricula + "s", campos[0].trim());
                String marca = (String.format("%1$-" + longitudMarca + "s", campos[1].trim()));
                String modelo = String.format("%1$-" + longitudModelo + "s", campos[2].trim());
                // Si no existe la matrícula, se añade
                if (existe(matricula.trim()) == -1 && !problemaCampos(matricula.trim(), marca.trim(), modelo.trim())) {
                    registroFormateado = matricula.concat(marca).concat(modelo);
                    listaRegistros.add(registroFormateado);
                }
            }
            contador++;
        }
        return new ArrayList<>(listaRegistros.reversed());
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

    /**
     * Esta función va a validar la posición pasada por parámetros,
     * comrpueba que no sea negativa ni mayor a los registros que hay
     *
     * @param posicion la posición a validar
     * @throws GestorBBDDException
     */
    private void validarPosicion(long posicion) throws GestorBBDDException {
        if (posicion < 0 || posicion > totalRegistros) {
            throw new GestorBBDDException("La posición no es válida");
        }
    }

    /**
     * Esta función va a formatear los registros dentro de la lista
     * que contiene todos los registros de la bbdd
     *
     * @return los registros formateados para mostrar
     */
    public String mostrarRegistros() throws IOException {
        try (RandomAccessFile leer = new RandomAccessFile(this.rutaFicheroDat, "r")) {
            ArrayList<String> registros = pasarRegistrosALista(leer);
            if (registros.isEmpty()) {
                return "No hay registros";
            }
            return registros.stream().map(s -> "Registro: ".concat(s.substring(0, longitudMatricula).trim())
                            .concat(" - ").concat(s.substring(longitudMatricula, longitudMarca).trim())
                            .concat(" - ").concat(s.substring(longitudMarca, longitudModelo * 2).trim()))
                    .collect(Collectors.joining("\n"));
        }
    }

    /**
     * Esta función va a validar cada campo de un registro,
     * devolverá true si hay problemas y false si no hay problemas
     *
     * @param matricula la matrícula del coche
     * @param marca     la marca del coche
     * @param modelo    el modelo del coche
     * @return si hay error o no
     * @throws GestorBBDDException
     */
    public boolean problemaCampos(String matricula, String marca, String modelo) {
        return matricula.isBlank() || matricula.getBytes().length > longitudMatricula
                || marca.isBlank() || marca.getBytes().length > longitudMarca
                || modelo.isBlank() || modelo.getBytes().length > longitudModelo;
    }
}