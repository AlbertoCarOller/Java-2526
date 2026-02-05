package schema;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import utils.ConfigLoader;

import java.io.IOException;

// Esta clase se va a encargar de crear la base de datos y sus collections (Tablas)
public class SchemaController {
    // Creamos el mongo cliente (necesario para poder trabajar con la base de datos, obtenemos la conexión con mongo)
    private final MongoClient mongoClient;

    public SchemaController() throws IOException {
        // Cargamos el mongoClient
        mongoClient = MongoClients.create(ConfigLoader.loadProperties().getProperty("URL"));
        ;
    }

    /**
     * Esta función va a crear la base de datos 'tienda_gaming'
     * a partir del MongoClient
     *
     * @return devuelve la base de datos
     */
    public MongoDatabase obtenerMongoDatabase() {
        /* Con getDatabase() obetenemos la instancia de la base de datos (el puntero) no se crea hasta que trabajamos
         con ella, al igual pasa con las collections (Tablas) */
        return mongoClient.getDatabase("tienda_gaming");
    }

    /**
     * Esta función va a crear a partir de la referencia de database
     * la tabla clientes
     *
     * @return MutableCollection<Document>, la tabla
     */
    public MongoCollection<Document> obtenerTablaClientes() {
        // Accedemos a la referencia (puntero) de la base de datos
        return obtenerMongoDatabase()
                // Creamos la tabla (collection) clientes
                .getCollection("clientes");
    }

    /**
     * Esta función va a crear la tabla Videojuegos
     *
     * @return la tabla videojuegos
     */
    public MongoCollection<Document> obtenerTablaVideojuegos() {
        return obtenerMongoDatabase().getCollection("videojuegos");
    }

    /**
     * Esta función va a crear la tabla
     * ventas
     *
     * @return va a devolver la tabla ventas
     */
    public MongoCollection<Document> obtenerTablaVentas() {
        return obtenerMongoDatabase().getCollection("ventas");
    }

    /**
     * Esta función va a cerrar la conexión con
     * el cliente mongo
     */
    public void cerrarConexion() {
        mongoClient.close();
    }
}