package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class ConfigLoader {

    public ConfigLoader() {
    }

    /**
     * Esta función va a recibir un properties, es este properties se cargará toda la información
     * del archivo .properties
     *
     * @param properties el objeto properties que recibe la información
     * @throws IOException
     */
    public static void cargarProperties(Properties properties) throws IOException {
        // Leemos el properties
        try (InputStreamReader leer = new InputStreamReader(new FileInputStream("src/main/resources/config.properties"))) {
            // Cargamos los datos del .properties en el objeto
            properties.load(leer);
        }
    }
}