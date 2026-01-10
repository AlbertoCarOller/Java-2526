package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class ConfigLoader {

    /**
     * Esta función va a cargar los datos del config.properties
     * en el Properties pasado pro parámetros
     *
     * @param p el Properties al cual se le va a cargar los datos
     * @throws IOException en caso de que haya algún problema al cargar los datos
     *                     o encontrar los archivos
     */
    public static void cargarPropierties(Properties p) throws IOException {
        // Leemos el config.properties
        try (InputStreamReader leer = new InputStreamReader(new FileInputStream("src/main/resources/config.properties"))) {
            // Cargamos los datos del config.properties en el objeto Properties
            p.load(leer);
        }
    }
}
