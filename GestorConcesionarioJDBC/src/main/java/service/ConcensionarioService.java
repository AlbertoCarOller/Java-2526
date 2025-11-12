package service;

import util.ConnectionController;
import util.SQLSentences;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ConcensionarioService {
    private ConnectionController connCont;

    public ConcensionarioService() throws IOException {
        connCont = new ConnectionController(); // -> Controlador de las conexiones con las bases de datos
    }

    /**
     * Se comprueba que la conexión se ha realizado correctamente
     *
     * @return true si la conexión funciona
     * @throws SQLException en caso de que la conexión no funcione, por lo que no devolverá nada
     */
    public Boolean comprobarConexionVacia() throws SQLException {
        try (Connection connection = connCont.getConnectionVacia()) {
        }
        // Si no ha saltado excepción devolverá true
        return true;
    }

    /**
     * Se comprueba que la conexión se ha realizado correctamente con MySQL a la base de datos
     *
     * @return true si la conexión funciona
     * @throws SQLException en caso de que la conexión no funcione, por lo que no devolverá nada
     */
    public Boolean comprobarConexionMySQL() throws SQLException {
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
    public Boolean comprobarConexionSQLite() throws SQLException {
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
    public void crearTablasMySQL() throws SQLException {
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
                // Se cierra la conexión
                connection.close();
                // Se devuelve el auto-commit a true
                connection.setAutoCommit(true);
            }
        }
    }

    /**
     * Esta función va a crear las tablas necesarias en SQLite
     *
     * @throws SQLException
     */
    public void crearTablasSQLite() throws SQLException {
        // Obtenemos la conexión en null
        Connection connection = null;
        // Obtenemos la conexión con SQLite
        try {
            connection = connCont.getConnectionSQLite();
            try (Statement statement = connection.createStatement()) {
                // Creamos las tablas
                statement.execute(SQLSentences.SQL_CREATE_TABLE_PROPIETARIOS_MYSQLITE);
                statement.execute(SQLSentences.SQL_CREATE_TABLE_COCHES_MYSQLITE);
                statement.execute(SQLSentences.SQL_CREATE_TABLE_TRASPASOS_MYSQLITE);
                connection.commit();
            }
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
                throw new SQLException(e.getMessage());
            }
        } finally {
            if (connection != null) {
                connection.close();
                connection.setAutoCommit(true);
            }
        }
    }

    // TODO: método privado que haga una transacción de las insercciones de los datos del CSV
}