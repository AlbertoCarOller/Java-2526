package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Esta clase simula un sistema de base de datos simple utilizando un único
 * fichero binario con registros de longitud fija.
 */
public class GestorBBDDP {
    // --- Atributos ---
    private final String rutaFichero;
    private final Map<String,Integer> esquemaDefinicionCampos;
    private final String nombreCampoClave;
    private int longitudRegistroEnBytes;
    private long totalRegistros;
    private long totalRegistrosMarcadosParaBorrado;

    // --- Getters ---
    public String getNombreCampoClave() {
        return nombreCampoClave;
    }
    public String getRutaFichero() {
        return rutaFichero;
    }
    public Map<String, Integer> getEsquemaDefinicionCampos() {
        return esquemaDefinicionCampos;
    }
    public int getLongitudRegistroEnBytes() {
        return longitudRegistroEnBytes;
    }
    public long getTotalRegistros() {
        return totalRegistros;
    }
    public long getTotalRegistrosMarcadosParaBorrado() {
        return totalRegistrosMarcadosParaBorrado;
    }

    /**
     * Constructor. Inicializa el gestor, calcula la longitud de los registros
     * y crea el fichero si no existe.
     * @param rutaFichero La ruta del fichero que actuará como base de datos.
     * @param esquemaDefinicionCampos Un mapa que define el nombre y tamaño de cada campo.
     * @param nombreCampoClave El nombre del campo que servirá como clave primaria.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    public GestorBBDDP(String rutaFichero, Map<String, Integer> esquemaDefinicionCampos, String nombreCampoClave) throws IOException {
        this.rutaFichero = rutaFichero;
        this.esquemaDefinicionCampos = esquemaDefinicionCampos;
        this.nombreCampoClave = nombreCampoClave;
        this.totalRegistros = 0;
        this.totalRegistrosMarcadosParaBorrado = 0;
        this.longitudRegistroEnBytes = 0;

        // Calcula la longitud total de un registro sumando la de sus campos.
        for (Map.Entry<String, Integer> campo : esquemaDefinicionCampos.entrySet()){
            longitudRegistroEnBytes += campo.getValue();
        }

        Path pathDelFichero = Paths.get(rutaFichero);
        if (Files.exists(pathDelFichero)){
            // Si el fichero existe, calcula cuántos registros contiene.
            totalRegistros = Files.size(pathDelFichero) / longitudRegistroEnBytes;
        } else {
            // Si no existe, lo crea vacío.
            Files.createFile(pathDelFichero);
        }
    }

    /**
     * Inserta un nuevo registro al final del fichero.
     * @param datosRegistro Un mapa que contiene los pares "nombreCampo" -> "valorCampo".
     * @return La posición del registro insertado (empezando en 0), o -1 si la clave ya existía.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    public long insertar(HashMap<String,String> datosRegistro) throws IOException{
        String valorClaveAInsertar = datosRegistro.get(this.nombreCampoClave);
        if (recuperar(valorClaveAInsertar) != null){
            System.err.println("No se pudo insertar registro, clave duplicada: " + valorClaveAInsertar);
            return -1;
        }

        Path pathDelFichero = Paths.get(rutaFichero);
        // Usamos try-with-resources para asegurar que el stream se cierra.
        // StandardOpenOption.APPEND asegura que escribimos al final.
        try(OutputStream streamSalida = Files.newOutputStream(pathDelFichero, StandardOpenOption.APPEND)){
            for (Map.Entry<String,Integer> campo : esquemaDefinicionCampos.entrySet()) {
                int longitudCampo = campo.getValue();
                String valorDelCampo = datosRegistro.get(campo.getKey());
                if (valorDelCampo == null){
                    valorDelCampo = "";
                }

                // Asegura que el valor ocupe el tamaño exacto, rellenando con espacios.
                String valorCampoFormateado = String.format("%1$-" + longitudCampo + "s", valorDelCampo);
                streamSalida.write(valorCampoFormateado.getBytes(StandardCharsets.UTF_8), 0, longitudCampo);
            }
        }

        this.totalRegistros++;
        return  this.totalRegistros - 1;
    }

    /**
     * Busca y recupera un registro basado en el valor de su campo clave.
     * @param valorClaveBuscado El valor de la clave del registro que se quiere encontrar.
     * @return Un mapa con los datos del registro, o null si no se encuentra.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    public Map<String,String> recuperar(String valorClaveBuscado) throws IOException{
        Path pathDelFichero = Paths.get(this.rutaFichero);
        try(InputStream streamEntrada = Files.newInputStream(pathDelFichero)){
            byte[] bufferRegistro = new byte[this.longitudRegistroEnBytes];

            // Leemos el fichero registro por registro
            for(int posicion = 0; posicion < this.totalRegistros; posicion++){
                if(streamEntrada.read(bufferRegistro, 0, this.longitudRegistroEnBytes) < this.longitudRegistroEnBytes){
                    return null; // Fin de fichero inesperado
                }

                // Extraemos el valor del campo clave del registro leído
                String valorClaveLeido = recuperarValorCampoClaveDesdeBuffer(bufferRegistro);

                // Comparamos con el valor buscado (quitando espacios extra)
                if(valorClaveBuscado.equals(valorClaveLeido.trim())){
                    Map<String,String> registroEncontrado = new HashMap<>();
                    int offset = 0;
                    // Si coincide, "desmontamos" el buffer completo en un mapa
                    for (Map.Entry<String,Integer> campo : esquemaDefinicionCampos.entrySet()) {
                        String nombreCampo = campo.getKey();
                        int longitudCampo = campo.getValue();
                        String valorCampo = new String(bufferRegistro, offset, longitudCampo, StandardCharsets.UTF_8).trim();
                        registroEncontrado.put(nombreCampo, valorCampo);
                        offset += longitudCampo;
                    }
                    return registroEncontrado;
                }
            }
            return null; // No se encontró tras recorrer todo el fichero
        }
    }

    /**
     * Modifica un campo específico de un registro existente. No se puede modificar el campo clave.
     * @param valorClaveBuscado El valor de la clave para identificar el registro a modificar.
     * @param nombreCampoAModificar El nombre del campo cuyo valor se cambiará.
     * @param nuevoValorCampo El nuevo valor para el campo.
     * @return true si la modificación tuvo éxito, false en caso contrario.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    public boolean modificar(String valorClaveBuscado, String nombreCampoAModificar, String nuevoValorCampo) throws IOException{
        if(nombreCampoAModificar.equals(this.nombreCampoClave)){
            System.err.println("Error: No se puede modificar el campo clave.");
            return false;
        }

        try (RandomAccessFile ficheroAccesoAleatorio = new RandomAccessFile(this.rutaFichero, "rws")) {
            for(int posicion = 0; posicion < this.totalRegistros; posicion++){
                // Saltamos al inicio del registro actual
                ficheroAccesoAleatorio.seek((long)posicion * longitudRegistroEnBytes);
                byte[] bufferRegistro = new byte[this.longitudRegistroEnBytes];
                ficheroAccesoAleatorio.read(bufferRegistro);

                String valorClaveLeido = recuperarValorCampoClaveDesdeBuffer(bufferRegistro);

                if(valorClaveBuscado.equals(valorClaveLeido.trim())){
                    int offsetCampoAModificar = 0;
                    for(Map.Entry<String,Integer> campo: esquemaDefinicionCampos.entrySet()){
                        String nombreCampoActual = campo.getKey();
                        int longitudCampo = campo.getValue();
                        if(nombreCampoAModificar.equals(nombreCampoActual)){
                            // Nos posicionamos al inicio del campo a modificar dentro del fichero
                            ficheroAccesoAleatorio.seek((long)posicion * longitudRegistroEnBytes + offsetCampoAModificar);
                            String valorCampoFormateado = String.format("%1$-" + longitudCampo + "s", nuevoValorCampo);
                            ficheroAccesoAleatorio.write(valorCampoFormateado.getBytes(StandardCharsets.UTF_8), 0, longitudCampo);
                            return true; // Modificación completada
                        }
                        offsetCampoAModificar += longitudCampo;
                    }
                }
            }
        }
        return false; // No se encontró el registro
    }

    /**
     * Marca un registro como borrado (borrado lógico), sobreescribiéndolo con bytes nulos.
     * @param valorClaveABorrar El valor de la clave del registro a borrar.
     * @return true si se encontró y marcó, false en caso contrario.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    public boolean borrar(String valorClaveABorrar) throws IOException {
        try (RandomAccessFile ficheroAccesoAleatorio = new RandomAccessFile(this.rutaFichero, "rws")) {
            for (int posicion = 0; posicion < this.totalRegistros; posicion++) {
                ficheroAccesoAleatorio.seek((long)posicion * this.longitudRegistroEnBytes);
                byte[] bufferRegistro = new byte[this.longitudRegistroEnBytes];
                ficheroAccesoAleatorio.read(bufferRegistro);

                String valorClaveLeido = recuperarValorCampoClaveDesdeBuffer(bufferRegistro);

                if (valorClaveABorrar.equals(valorClaveLeido.trim())) {
                    // Volvemos a la posición del registro y lo sobreescribimos con ceros
                    ficheroAccesoAleatorio.seek((long)posicion * this.longitudRegistroEnBytes);
                    ficheroAccesoAleatorio.write(new byte[this.longitudRegistroEnBytes]);
                    this.totalRegistrosMarcadosParaBorrado++;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Compacta el fichero, eliminando físicamente los registros marcados como borrados.
     * @return El número de registros que han sido purgados.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    public int compactar() throws IOException {
        if (this.totalRegistrosMarcadosParaBorrado == 0) {
            return 0; // Nada que hacer
        }

        int contadorRegistrosSuprimidos = 0;
        Path ficheroOriginalPath = Paths.get(rutaFichero);
        // Creamos un fichero temporal para escribir los registros válidos.
        Path ficheroTemporalPath = Files.createTempFile("GestorBBDD_compact_", ".tmp");

        try (InputStream streamEntrada = Files.newInputStream(ficheroOriginalPath);
             OutputStream streamSalida = Files.newOutputStream(ficheroTemporalPath)) {

            byte[] bufferRegistro = new byte[this.longitudRegistroEnBytes];
            for (int pos = 0; pos < this.totalRegistros; pos++) {
                streamEntrada.read(bufferRegistro);
                boolean esRegistroBorrado = true;
                // Un registro está borrado si todos sus bytes son 0.
                for (byte b : bufferRegistro) {
                    if (b != 0) {
                        esRegistroBorrado = false;
                        break;
                    }
                }
                if (!esRegistroBorrado) {
                    streamSalida.write(bufferRegistro);
                } else {
                    contadorRegistrosSuprimidos++;
                }
            }
        }

        // Hacemos una copia de seguridad del fichero original antes de reemplazarlo.
        String nombreCopiaSeguridad = rutaFichero + "." + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".bak";
        Path ficheroCopiaSeguridadPath = Paths.get(nombreCopiaSeguridad);
        Files.move(ficheroOriginalPath, ficheroCopiaSeguridadPath);

        // El fichero temporal, ya limpio, se convierte en el nuevo fichero original.
        Files.move(ficheroTemporalPath, ficheroOriginalPath);

        // Actualizamos los contadores de la clase.
        this.totalRegistros -= contadorRegistrosSuprimidos;
        this.totalRegistrosMarcadosParaBorrado = 0;
        return contadorRegistrosSuprimidos;
    }

    /**
     * Método auxiliar privado para extraer el valor del campo clave de un buffer de bytes.
     * @param bufferRegistro Array de bytes que representa un registro completo.
     * @return El valor del campo clave como String.
     */
    private String recuperarValorCampoClaveDesdeBuffer(byte[] bufferRegistro) {
        int offset = 0;
        for (Map.Entry<String, Integer> campo : esquemaDefinicionCampos.entrySet()) {
            String nombreCampo = campo.getKey();
            int longitudCampo = campo.getValue();
            if (nombreCampo.equals(this.nombreCampoClave)) {
                return new String(bufferRegistro, offset, longitudCampo, StandardCharsets.UTF_8);
            }
            offset += longitudCampo;
        }
        return ""; // No debería ocurrir si el esquema está bien definido.
    }


}