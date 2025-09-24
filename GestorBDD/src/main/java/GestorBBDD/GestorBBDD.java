package GestorBBDD;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class GestorBBDD {
    private final int longitudMatricula = 7;
    private final int longitudMarca = 32;
    private final int longitudModelo = 32;
    private final String rutaFicheroDat;
    private long totalRegistros = 0;
    private long registrosEnBytes = 0;

    // Creamos el constructor
    public GestorBBDD(String rutaFicheroDat) throws IOException {
        this.rutaFicheroDat = rutaFicheroDat;
        if (new File(this.rutaFicheroDat).exists()) {
            totalRegistros = (longitudMarca + longitudMatricula + longitudModelo)
                    / Files.size(new File(this.rutaFicheroDat).toPath());

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
    public void insertarRegistro(String matricula, String marca, String modelo) throws IOException, GestorBBDDException {
        if (matricula.isBlank() || matricula.getBytes().length > longitudMatricula
                || marca.isBlank() || marca.getBytes().length > longitudMarca
                || modelo.isBlank() || modelo.getBytes().length > longitudModelo) {
            throw new GestorBBDDException("Algún campo es inválido");
        }
        if (existe(matricula)) {
            throw new GestorBBDDException("No se permiten registros duplicados");

        } else {
            try (RandomAccessFile rd = new RandomAccessFile(this.rutaFicheroDat, "w")) {
                // Nos posicionamos al final para escribir el nuevo registro
                rd.seek(this.registrosEnBytes);
                rd.writeUTF(matricula);
                escribirEspaciosEnBlanco(longitudMatricula - matricula.getBytes().length, rd);
                rd.writeUTF(marca);
                escribirEspaciosEnBlanco(longitudMarca - marca.getBytes().length, rd);
                rd.writeUTF(modelo);
                escribirEspaciosEnBlanco(longitudModelo - modelo.getBytes().length, rd);
                this.registrosEnBytes += 71; // Sumamos 71 que es lo que ocupa en bytes un registro
            }
        }
    }

    /**
     * Esta función escribe en la base de datos los espacios en blanco sobrantes de los bytes
     * reservados para cada campo
     *
     * @param espaciosEnBlanco el número de espacios en blanco a escribir
     * @param rd               el RandomAccessFile
     * @throws IOException
     */
    private void escribirEspaciosEnBlanco(int espaciosEnBlanco, RandomAccessFile rd) throws IOException {
        for (int i = 0; i < espaciosEnBlanco; i++) {
            rd.writeUTF(" ");
        }
    }

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
            // Mientras no hayamos llegado al final seguimos leyendo
            while (rd.read(bufferMatricula) != -1) {
                // Nos adelantamos 64 bytes hasta llegar a la siguiente matrícula (si no fuera el final)
                rd.seek(64);
                String matriculaS = new String(bufferMatricula, StandardCharsets.UTF_8);
                if (matriculaS.equals(matricula)) {
                    return true;
                }
            }
        }
        return false;
    }
}