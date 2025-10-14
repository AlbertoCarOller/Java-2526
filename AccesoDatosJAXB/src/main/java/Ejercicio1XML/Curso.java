package Ejercicio1XML;

import jakarta.xml.bind.annotation.*;

import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "curso") // -> Le indicamos que esta clase va a ser la raíz del XML
public class Curso {
    // Creamos los atributos
    @XmlAttribute(name = "id")
    private int id;
    @XmlElement(name = "nombre")
    private String nombre;
    @XmlElementWrapper(name = "estudiantes") /* -> Esta es una anotación envoltorio, crea un envoltorio llamado
     estudiantes y dentro de esta los elementos, cada estudiante*/
    @XmlElement(name = "estudiante") // -> Los elementos estudiante dentro del envoltorio de estudiantes
    private List<Estudiante> estudiantes;

    // Creamos el constructor
    public Curso(int id, String nombre, List<Estudiante> estudiantes) {
        this.id = id;
        this.nombre = nombre;
        this.estudiantes = estudiantes;
    }

    // Creamos el constructor vacío
    public Curso() {
    }

    // Creamos los getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Estudiante> getEstudiantes() {
        return estudiantes;
    }

    public void setEstudiantes(List<Estudiante> estudiantes) {
        this.estudiantes = estudiantes;
    }

    @Override
    public String toString() {
        return "Ejercicio1XML.Curso{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", estudiantes=" + estudiantes +
                '}';
    }
}
