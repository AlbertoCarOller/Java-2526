package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

// La clase para cargar en un properties la URL del cliente Mongo
public class ConfigLoader {

    /**
     * Esta función va a cargar los datos del 'config.properties'
     * en un properties que creamos en la función, posteriormente
     * lo devolvemos
     *
     * @return el Properties con los datos
     * @throws IOException en caso de cualquier error
     */
    public static Properties loadProperties() throws IOException {
        // Creamos el properties
        Properties prop = new Properties();
        // Creamos el InputStream para leer dentro del config.properties dentro del try porque es Closeable
        try (InputStream in = new FileInputStream("src/resources/config.properties")) {
            // Cargamos los datos
            prop.load(in);
        }
        // Devolvemos el properties con los datos ya cargados
        return prop;
    }
}
