package service;

import util.ConnectionController;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ConcensionarioService {
    private ConnectionController connCont;

    public ConcensionarioService() throws IOException {
        connCont = new ConnectionController();
    }

    /**
     * Se comprueba que la conexión se ha realizado correctamente
     *
     * @throws SQLException
     */
    public void comprobarConexionVacia() throws SQLException {
        connCont.getConnectionVacia();
        System.out.println("Conexión realizada con éxito (vacía)");
    }

    /**
     * Se comprueba que la conexión se ha realizado correctamente con MySQL a la base de datos
     *
     * @throws SQLException
     */
    public void comprobarConexionMySQL() throws SQLException {
        connCont.getConnectionMySQL();
        System.out.println("Conexión realizada con éxito (MySQL)");
    }

    /**
     * Se comprueba que la conexión se ha realizado correctamente con SQLite
     *
     * @throws SQLException
     */
    public void comprobarConexionSQLite() throws SQLException {
        connCont.getConnectionSQLite();
        System.out.println("Conexión realizada con éxito (SQLite)");
    }

    /**
     * Esta función va a crear el esquema de la base de datos,
     * en caso de que ya exista la base de datos, la elimina
     *
     * @throws SQLException
     */
    private void crearEsquema() throws SQLException {
        try (Connection connection = connCont.getConnectionVacia()) {
            // Se crea la base de datos
            Statement statement = connection.createStatement();
            statement.execute("CREATE DATABASE IF NOT EXISTS concesionario_db");
        }
    }

    /**
     * Esta función va a crear las 3 tablas que conforman
     * la estructura de la base de datos
     *
     * @throws SQLException
     */
    public void crearTablasMySQL() throws SQLException {
        // Creamos el esquema antes de crear las tablas
        crearEsquema(); // TODO: mover fuera para poder comprobar la conexión con la base de datos
        // Obtenemos la conexión a la base de datos una vez creada
        try (Connection connection = connCont.getConnectionMySQL()) {
            Statement statement = connection.createStatement();
            // Creamos las tablas
            statement.execute("CREATE TABLE IF NOT EXISTS propietarios (" +
                    "    id_propietario INT PRIMARY KEY AUTO_INCREMENT," +
                    "    dni            VARCHAR(10) NOT NULL UNIQUE," +
                    "    nombre         VARCHAR(100) NOT NULL," +
                    "    apellidos      VARCHAR(150) NOT NULL," +
                    "    telefono       VARCHAR(15)" +
                    ");");
            statement.execute("CREATE TABLE IF NOT EXISTS coches (" +
                    "    matricula       VARCHAR(10) PRIMARY KEY," +
                    "    marca           VARCHAR(50) NOT NULL," +
                    "    modelo          VARCHAR(50) NOT NULL," +
                    "    extras          VARCHAR(255),\n" +
                    "    precio          DECIMAL(10, 2) NOT NULL," +
                    "    id_propietario  INT," +
                    "    FOREIGN KEY (id_propietario) REFERENCES propietarios(id_propietario)" +
                    ");");
            statement.execute("CREATE TABLE IF NOT EXISTS traspasos (" +
                    "    id_traspaso     INT PRIMARY KEY AUTO_INCREMENT," +
                    "    matricula_coche VARCHAR(10) NOT NULL," +
                    "    id_vendedor     INT NOT NULL," +
                    "    id_comprador    INT NOT NULL," +
                    "    monto_economico DECIMAL(10, 2) NOT NULL," +
                    "    FOREIGN KEY (matricula_coche) REFERENCES coches(matricula)," +
                    "    FOREIGN KEY (id_vendedor)     REFERENCES propietarios(id_propietario)," +
                    "    FOREIGN KEY (id_comprador)    REFERENCES propietarios(id_propietario)" +
                    ");");
        }
    }
}
