package org.example.practicaguiadamongodb.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
@Document(collection = "alumnos") // Nombre de la colección en Mongo
public class Alumno {
    @Id
    private String id; // Spring maneja el ObjectId automáticamente como String
    private String nombre;
    private Double nota;
    private boolean repetidor;
    private List<String> asignaturas;
    // Constructores, Getters y Setters
    // (Usa Alt+Insert en IntelliJ para generarlos rápido o Lombok @Data)
    public Alumno() {} // Constructor vacío obligatorio
    public Alumno(String nombre, Double nota, boolean repetidor, List<String>
            asignaturas) {
        this.nombre = nombre;
        this.nota = nota;
        this.repetidor = repetidor;
        this.asignaturas = asignaturas;
    }
    // ... Getters y Setters aquí ...
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Double getNota() { return nota; }
    public void setNota(Double nota) { this.nota = nota; }
    public boolean isRepetidor() { return repetidor; }
    public void setRepetidor(boolean repetidor) { this.repetidor = repetidor; }
    @Override
    public String toString() {
        return "Alumno{nombre='" + nombre + "', nota=" + nota + "}";
    }
}
