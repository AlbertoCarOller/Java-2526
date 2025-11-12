package service;

import model.Coche;
import util.ConfigLoader;
import util.ConnectionController;
import util.SQLSentences;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class ConcensionarioService {
    private ConnectionController connCont;
    private Properties prop;

    public ConcensionarioService() throws IOException {
        connCont = new ConnectionController(); // -> Controlador de las conexiones con las bases de datos
        prop = new Properties(); // -> Properties que vamos a utilizar para cargar los datos del .properties
        ConfigLoader.cargarProperties(prop); // -> Cargamos los datos en el properties
    }

    /**
     * Se comprueba que la conexión se ha realizado correctamente
     *
     * @return true si la conexión funciona
     * @throws SQLException en caso de que la conexión no funcione, por lo que no devolverá nada
     */
    /*public Boolean comprobarConexionVacia() throws SQLException {
        try (Connection connection = connCont.getConnectionVacia()) {
        }
        // Si no ha saltado excepción devolverá true
        return true;
    }*/

    /**
     * Se comprueba que la conexión se ha realizado correctamente con MySQL a la base de datos
     *
     * @return true si la conexión funciona
     * @throws SQLException en caso de que la conexión no funcione, por lo que no devolverá nada
     */
    private Boolean comprobarConexionMySQL() throws SQLException {
        // Se crea el esquema para comprobar la conexión
        crearEsquema();
        try (Connection connection = connCont.getConnectionMySQL()) {
        }
        // Si no ha saltado excepción devolverá true
        return true;
    }

    /**
     * Se comprueba que la conexión se ha realizado correctamente con SQLite
     *
     * @return true si la conexión funciona
     * @throws SQLException en caso de que la conexión no funcione, por lo que no devolverá nada
     */
    private Boolean comprobarConexionSQLite() throws SQLException {
        try (Connection connection = connCont.getConnectionSQLite()) {
        }
        // Si no ha saltado excepción devolverá true
        return true;
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
            statement.execute(SQLSentences.SQL_CREATE_SCHEME_MYSQL);
        }
    }

    /**
     * Esta función va a crear las 3 tablas que conforman
     * la estructura de la base de datos
     *
     * @throws SQLException
     */
    private void crearTablasMySQL() throws SQLException {
        // Creamos la conexión nula
        Connection connection = null;
        try {
            // Inicializamos la conexión
            connection = connCont.getConnectionMySQL();
            try (Statement statement = connection.createStatement()) {
                // Creamos una transacción de todo o nada
                connection.setAutoCommit(false);
                // Creamos las tablas
                statement.execute(SQLSentences.SQL_CREATE_TABLE_PROPIETARIOS_MYSQL);
                statement.execute(SQLSentences.SQL_CREATE_TABLE_COCHES_MYSQL);
                statement.execute(SQLSentences.SQL_CREATE_TABLE_TRASPASOS_MYSQL);
                // Hacemos commit si no ha saltado excepción
                connection.commit();
            }
        } catch (SQLException e) {
            // En caso de que no sea nula la conexión y haya excepción, se hace rollback
            if (connection != null) {
                connection.rollback();
                throw new SQLException(e.getMessage()); // -> Lanzar excepción con el mensaje de error
            }
        } finally { // -> Garantiza que se cierre la conexión y se ponga el auto-commit a true, pase lo que pase
            // En caso de que no sea nula la conexión
            if (connection != null) {
                // Se devuelve el auto-commit a true
                connection.setAutoCommit(true);
                // Se cierra la conexión
                connection.close();
            }
        }
    }

    /**
     * Esta función va a crear las tablas necesarias en SQLite
     *
     * @throws SQLException
     */
    private void crearTablasSQLite() throws SQLException {
        // Obtenemos la conexión en null
        Connection connection = null;
        // Obtenemos la conexión con SQLite
        try {
            connection = connCont.getConnectionSQLite();
            try (Statement statement = connection.createStatement()) {
                connection.setAutoCommit(false);
                // Creamos las tablas
                statement.execute(SQLSentences.SQL_CREATE_TABLE_PROPIETARIOS_MYSQLITE);
                statement.execute(SQLSentences.SQL_CREATE_TABLE_COCHES_MYSQLITE);
                statement.execute(SQLSentences.SQL_CREATE_TABLE_TRASPASOS_MYSQLITE);
                // Hacemos el commit
                connection.commit();
            }
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
                throw new SQLException(e.getMessage());
            }
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }


    /**
     * Insertamos los datos obtenidos del CSV directamente en la base de datos con
     * sentencias SQL, las sentencias en este caso para los insert son iguales
     * tanto en MySQL como en SQLite
     *
     * @param coches la lista de coches a insertar
     * @param mysql  si la conexión va a ser con MySQL o no
     * @throws SQLException
     */
    private void insertarDatosCSV(List<Coche> coches, Boolean mysql) throws SQLException {
        // Creamos una conexión
        Connection connection = null;
        try {
            // Si es MySQL obtenemos la conexión con la base de datos del servidor y si no pues con SQLite
            if (mysql) connection = connCont.getConnectionMySQL();
            else connection = connCont.getConnectionSQLite();
            // Creamos el PreparedStatement con la sentencia SQL para insertar
            try (PreparedStatement preparedStatement = connection.prepareStatement(SQLSentences.SQL_INSERT_COCHE_SQL)) {
                // Ponemos el auto-commit a false
                connection.setAutoCommit(false);
                for (Coche coche : coches) {
                    preparedStatement.setString(1, coche.getMatricula());
                    preparedStatement.setString(2, coche.getMarca());
                    preparedStatement.setString(3, coche.getModelo());
                    // Si no hay extras, se inserta NULL (el campo es nullable, así que no hay problema)
                    if (coche.getExtras().isEmpty()) preparedStatement.setNull(4, java.sql.Types.NULL);
                        // En caso de que haya extras, se crea un String formateado con los extras de la lista de extras
                    else preparedStatement.setString(4, String.join(", ", coche.getExtras()));
                    preparedStatement.setDouble(5, coche.getPrecio());
                    // Añadimos cada statement al batch
                    preparedStatement.addBatch();
                }
                // Una vez añadido todos los statement, los ejecutamos todos a la vez
                preparedStatement.executeBatch();
                // Confirmamos, hacemos un commit
                connection.commit();
            }
            /* Aquí no lanzamos la excepción porque en este caso no queremos informar de posibles
             * errores, la excepción que podría atraparse sería que cuando se crea por primera vez
             * la base de datos con los datos del CSV, si volvemos a pulsar esta opción, lo que es
             * la creación de las tablas y la base de datos no dará SQLException porque se controlan
             * con las sentencias SQL, pero se intentará hacer los insert de los datos del CSV, esto
             * lanzará excepción, aquí la atrapamos y la silenciamos */
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }

    /**
     * Esta función va a devolver una lista de coches, va a crear objetos Coche
     * a partir cada línea del CSV
     *
     * @return una lista de coches
     * @throws IOException
     */
    private List<Coche> importarCochesCSV() throws IOException {
        // Creamos la lista a devolver
        List<Coche> coches = new ArrayList<>();
        // Obtenemos todas las líneas del fichero
        List<String> lineas = Files.readAllLines(Path.of(prop.getProperty("pathCSV")));
        // Quitamos la primera hora
        lineas.removeFirst();
        lineas.forEach(line -> {
            // Lista de los campos
            List<String> campos = Arrays.stream(line.split(";"))
                    .filter(c -> !c.isBlank()).toList();
            // Si hay 4 campos significa que no hay extras
            if (campos.size() == 4) {
                coches.add(new Coche(campos.getFirst().trim(), campos.get(1).trim(), campos.get(2).trim(), new ArrayList<>(),
                        Double.parseDouble(campos.getLast().trim())));

                // Si hay 5 campos significa que tiene extras
            } else if (campos.size() == 5) {
                coches.add(new Coche(campos.getFirst().trim(), campos.get(1).trim(), campos.get(2).trim(),
                        Arrays.stream(campos.get(3).trim().split("\\|")).map(String::trim).collect(Collectors
                                .toCollection(ArrayList::new)), Double.parseDouble(campos.getLast().trim())));
            }
        });
        return coches; // -> Devolvemos la lista con los coches ya formados
    }

    /**
     * Esta función recoge todas las acciones que debe hacer la primera opción del menú
     * dependiendo del tipo de conexión que se quiera se hará de una forma u otra
     *
     * @param mysql si la conexión es mysql o no
     * @return true si la conexión es correcta
     * @throws SQLException
     * @throws IOException
     */
    public boolean inciarDataBase(Boolean mysql) throws SQLException, IOException {
        Boolean conexionCorrecta; // Si la conexión es correcta
        // Si nos queremos conectar con MySQL
        if (mysql) {
            conexionCorrecta = comprobarConexionMySQL();
            crearTablasMySQL(); // -> Creamos las tablas MySQL
            insertarDatosCSV(importarCochesCSV(), true); // -> Insertamos los datos del CSV
            // Nos conectamos con SQLite
        } else {
            conexionCorrecta = comprobarConexionSQLite();
            crearTablasSQLite(); // -> Creamos las tablas SQLite
            insertarDatosCSV(importarCochesCSV(), false); // -> Insertamos los datos del CSV
        }
        return conexionCorrecta;
    }
}