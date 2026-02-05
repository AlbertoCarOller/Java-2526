package service;

import org.bson.Document;
import schema.SchemaController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class GestorTienda {
    // Creamos el SchemaController, esta tiene las referencias necesarias de la base de datos
    SchemaController schemaController;

    // Creamos el constructor
    public GestorTienda() throws IOException {
        // Creamos el controller del esquema, carga a su vez (en su constructor) en MongoClient, este es estático
        this.schemaController = new SchemaController();
    }

    /**
     * Esta función va a borrar la base de datos y con ella su contenido,
     * posteriormente se van a insertar datos de prueba con insertMany()
     */
    public void cargarDatosSemilla() {
        // Obtenemos la base de datos, la borramos en caso de que exista
        schemaController.obtenerMongoDatabase().drop();
        // IMPORTANTE: LA BASE DE DATOS SE "CREA" AL "CREAR" LA COLECCIÓN
        // Creamos los documentos (campos/registros) de las collections (videojuegos y clientes)
        schemaController.obtenerTablaVentas().insertMany(List.of(new Document("nombre", "Paco")
                        .append("email", "paco@gmail.com").append("fecha_registro", LocalDate.now()),
                new Document("nombre", "Tobio")
                        .append("email", "tobio@gmail.com").append("fecha_registro", LocalDate.now())));
        // -- Videojuegos--
        schemaController.obtenerTablaVideojuegos().insertMany(List.of(new Document("titulo", "Fallout 4")
                .append("genero", "Rol").append("precio", 13.88).append("stock", 6), new Document("titulo", "Fallout 3")
                .append("genero", "Rol").append("precio", 2.69).append("stock", 12), new Document("titulo", "Fallout New Vegas")
                .append("genero", "Rol").append("precio", 7.82).append("stock", 2), new Document("titulo", "Fallout 76")
                .append("genero", "Rol").append("precio", 39.99).append("stock", 20), new Document("titulo", "Fallout Shelter")
                .append("genero", "Gestion").append("precio", 0.8).append("stock", 10)));
    }
}
