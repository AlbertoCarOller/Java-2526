package util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionController {
    private Properties prop;

    public ConnectionController() throws IOException {
        // Creamos el properties
        prop = new Properties();
        // Cargamos el contenido del properties
        ConfigLoader.cargarProperties(prop);
    }

    /**
     * Esta función va a devolver una conexión sin base de datos seleccionada, esta es
     * la conexión que se encargará de crear el esquema de la base de datos MySQL
     *
     * @return la conexión
     * @throws IOException
     * @throws SQLException
     */
    public Connection getConnectionVacia() throws SQLException {
        return DriverManager.getConnection(prop
                        .getProperty("urlVacio"),
                prop.getProperty("userVacio"),
                prop.getProperty("passwordVacio"));
    }

    /**
     * Esta función va a devolver la conexión con la base de datos del servidor,
     * es decir la conexión con la base de datos MySQL
     *
     * @return la conexión
     * @throws SQLException
     */
    public Connection getConnectionMySQL() throws SQLException {
        return DriverManager.getConnection(prop.getProperty("urlMySQL"),
                prop.getProperty("userMySQL"), prop.getProperty("passwordMySQL"));
    }

    /**
     * Esta función va a devolver una conexión con la base de datos SQLite
     *
     * @return la conexión
     * @throws SQLException
     */
    public Connection getConnectionSQLite() throws SQLException {
        return DriverManager.getConnection(prop.getProperty("urlSQLite"));
    }
}