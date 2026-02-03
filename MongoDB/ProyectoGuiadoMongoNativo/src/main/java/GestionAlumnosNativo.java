import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Arrays;
// Importaciones estáticas para filtros y updates (Hacen el código más legible)
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import static com.mongodb.client.model.Projections.*;

public class GestionAlumnosNativo {
    public static void main(String[] args) {
// 1. CONEXIÓN
// Cambia la URI si no activaste la seguridad: "mongodb://localhost:27017"
        String connectionString = "mongodb://root:root@localhost:27017";
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
// Conectar a BBDD y Colección
            MongoDatabase database = mongoClient.getDatabase("instituto_dam");
            MongoCollection<Document> collection = database.getCollection("alumnos");
            System.out.println("--> Conexión establecida.");
// 2. LIMPIEZA (Para empezar de cero cada vez que ejecutemos)
            collection.drop();
            System.out.println("--> Colección limpiada.");
// 3. INSERCIÓN (CREATE)
// Creamos documentos BSON manualmente
            Document alumno1 = new Document("nombre", "Ana")
                    .append("nota", 4.5)
                    .append("repetidor", false)
                    .append("asignaturas", Arrays.asList("AD", "PSP")); // Array
            Document alumno2 = new Document("nombre", "Luis")
                    .append("nota", 8.0)
                    .append("repetidor", false)
                    .append("asignaturas", Arrays.asList("DI", "SGE"));
            Document alumno3 = new Document("nombre", "Eva")
                    .append("nota", 3.0)
                    .append("repetidor", true) // Es repetidora
                    .append("asignaturas", Arrays.asList("AD"));
            collection.insertMany(Arrays.asList(alumno1, alumno2, alumno3));
            System.out.println("--> 3 Alumnos insertados.");
// 4. MODIFICACIÓN (UPDATE)
// Regla de negocio: Subir 1 punto a los NO repetidores
            System.out.println("--> Aplicando subida de nota...");
            collection.updateMany(
                    eq("repetidor", false), // Filtro: WHERE repetidor = false
                    inc("nota", 1.0) // Operación: SET nota = nota + 1
            );
// 5. CONSULTA (READ)
// Listar solo nombre y nota de los aprobados (> 5.0)
            System.out.println("\n--- ALUMNOS APROBADOS ---");
            collection.find(gt("nota", 5.0))
                    .projection(fields(include("nombre", "nota"), excludeId()))
                    .forEach(doc -> System.out.println(doc.toJson()));
        } catch (Exception e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }
}