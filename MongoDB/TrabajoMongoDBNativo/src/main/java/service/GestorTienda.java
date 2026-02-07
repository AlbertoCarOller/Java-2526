package service;

import com.mongodb.client.model.Projections;
import exception.TiendaException;
import org.bson.Document;
import schema.SchemaController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Updates.inc;

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
        schemaController.obtenerTablaClientes().insertMany(List.of(new Document("nombre", "Paco")
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
        // Se crea la collection
    }

    /**
     * Esta función va a insertar un videojuego en caso de
     * que el precio sea mayor o igual a 0 (validación requerida en el enunciado)
     *
     * @param titulo el título del juego
     * @param genero el género del juego
     * @param precio el precio del juego
     * @param stock  el stock del juego
     * @throws TiendaException en caso de que el precio sea menor a 0
     */
    public void insertarJuego(String titulo, String genero, double precio, int stock) throws TiendaException {
        // Si el precio del videojuego es menor a 0 lanzamos excepción
        if (precio < 0) {
            throw new TiendaException("El precio del videojuego no puede ser negativo");
        }
        // Comprobamos como extra que el stock no sea menor a 0
        if (stock < 0) {
            throw new TiendaException("El stock no puede ser negativo");
        }
        // Con insertOne() insertamos un documento, un campo, el videojuego
        schemaController.obtenerTablaVideojuegos().insertOne(new Document("titulo", titulo)
                .append("genero", genero).append("precio", precio).append("stock", stock));
    }

    /**
     * Esta función va a insertar un nuevo cliente en la base de datos
     * en caso de que su correo contenga una @ (validación requerida en el enunciado)
     *
     * @param nombre el nombre del cliente
     * @param email  el email del cliente
     * @throws TiendaException en caso de que esté mal formado el email
     */
    public void insertarCliente(String nombre, String email) throws TiendaException {
        // En caso de que el email no sea válido, lanzamos excepción
        if (!email.matches("^.*@.*$")) {
            throw new TiendaException("El correo está mal formado");
        }
        // Insertamos al cliente
        schemaController.obtenerTablaClientes().insertOne(new Document("nombre", nombre)
                .append("email", email).append("fecha_registro", LocalDate.now()));
    }

    /**
     * Esta función va a simular una transacción de manera manual,
     * se van a buscar un cliente y videojuego, si existen, se comprobará
     * que haya stock del videojuego, en caso de que sí, se creará una venta
     * y se restará -1 el stock
     *
     * @param emailCliente     el email del cliente a buscar
     * @param tituloVideojuego el título del videojuego a buscar
     * @throws TiendaException en caso de cualquier error de negocio
     */
    public void realizarVenta(String emailCliente, String tituloVideojuego) throws TiendaException {
        // Recuperamos Document del cliente y videojuego concreto, en caso de que no exista devuelve null
        Document cliente = schemaController.obtenerTablaClientes()
                // find() para buscar un Document/ Registro en concreto 'eq()' es 'igual'
                .find(eq("email", emailCliente)).first();
        Document videojuego = schemaController.obtenerTablaVideojuegos()
                .find(eq("titulo", tituloVideojuego)).first();
        // En caso de que alguno de los dos sea null lanzamos excepción, porque no se habrá/n encontrado
        if (cliente == null || videojuego == null) {
            throw new TiendaException("No se encuentra al cliente y/o videojuego");
        }
        // Comprobamos que el stock del videojuego no sea 0 para poder venderse una unidad
        if (videojuego.getInteger("stock") == 0) {
            throw new TiendaException("El stock no puede ser negativo");
        }
        // Creamos la venta
        schemaController.obtenerTablaVentas().insertOne(new Document("fecha_actual", LocalDate.now())
                // getObjectId() -> Devuelve el id auto-generado por Mongo
                .append("cliente_id", cliente.getObjectId("_id"))
                .append("juego_id", videojuego.getObjectId("_id"))
                /* El Patrón Snapshot ocurre solo, ya que el momento en el que se guarda el título
                 y precio es en ese momento instante y así queda registrado, aunque en el futuro cambie
                 se mantienen los valores del momento del registro */
                .append("titulo_snapshot", videojuego.getString("titulo"))
                .append("precio_snapshot", videojuego.getDouble("precio")));
        // Actualizamos el stock del videojuego, le quitamos una unidad, actualizamos con updateOne()
        schemaController.obtenerTablaVideojuegos()
                // Utilizamos el inc() en vez de para incrementar para decrementar, por eso le ponemos un -1
                .updateOne(eq("titulo", tituloVideojuego), inc("stock", -1));
    }

    /**
     * Esta función va buscar los videojuegos que hayan sido vendidos
     * a un cliente específico
     *
     * @param emailCliente el email del cliente
     * @return una lista de String con la lista de videojuegos
     * @throws TiendaException en caso de un error de negocio
     */
    public List<String> mostrarHistoralCliente(String emailCliente) throws TiendaException {
        // Recuperamos al cliente en caso de que exista
        //Document cliente = schemaController.obtenerTablaClientes().find(eq("email", emailCliente)).first();
        // En caso de que el cliente no exista lanzamos excepción
//        if (cliente == null) {
//            throw new TiendaException("No se encuentra al cliente con email " + emailCliente);
//        }
        // Recuperamos los nombres de los juegos que han sido comprados por dicho cliente
//        return schemaController.obtenerTablaVentas()
//                .find(eq("cliente_id", cliente.getObjectId("_id")))
//                .map(doc -> Projections.exclude("_id", "precio", "fecha_actual").toString()).iterator();
        /* ----------------------------- OTRA FORMA ----------------------------- */
        // Recuperamos el id del cliente
        Document cliente = schemaController.obtenerTablaClientes().find(eq("email", emailCliente))
                // projection() -> Permite incluir el solo el id de cada Document que va iterando
                .projection(Projections.include("_id")).first();
        // En caso de que no exista el cliente lanzamos excepción
        if (cliente == null) {
            throw new TiendaException("No se ha encontrado al cliente");
        }
        // Devolvemos una lista gracias a la función into() -> Al cual le pasamos new ArrayList<> para que cree la lista
        List<String> titulos = schemaController.obtenerTablaVentas()
                .find(eq("cliente_id", cliente.getObjectId("_id")))
                .map(document -> document.getString("titulo_snapshot")).into(new ArrayList<>());
        // En caso de que no haya videojuegos asociados a un cliente muestra que no hay datos
        if (titulos.isEmpty()) {
            return List.of("No hay datos");
        }
        // En caso de que sí haya datos, los devuelve
        return titulos;
    }

    /**
     * Esta función va a guardar el título y precio de los videojuegos
     * que tengan un precio menor a 25 €
     *
     * @return una lista con los pares título-precio de cada videojuego
     */
    public List<Map.Entry<String, Double>> mostrarJuegosMenor25() {
        /* Creamos una lista donde vamos a almacenar las entradas, los pares key-value de la
         información que queremos de cada videojuego */
        List<Map.Entry<String, Double>> lista = new ArrayList<>();
        // Vamos guardando en la lista las entradas
        schemaController.obtenerTablaVideojuegos().find(lt("precio", 25))
                .projection(Projections.exclude("id", "fecha_actual", "stock"))
                .map(document -> Map.entry(document.getString("titulo"), document.getDouble("precio")))
                .forEach(lista::add);
        // Devolvemos la lista
        return lista;
    }

    /**
     * Esta función va a cerrar la conexión con
     * el cliente mongo
     */
    public void cerrarConexion() {
        schemaController.mongoClient.close();
    }
}