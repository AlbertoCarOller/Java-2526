package Ejercicio1XML;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD) /* -> esto quiere decir que use directamente a sus campos indiferentemente
 de su seguridad, por ejemplo a diferencia de .PROPERTY que necesita de los getters y setters para acceder a los campos */
public class Estudiante {
    // Creamos los atributos
    @XmlAttribute(name = "dni") // -> definimos el dni como atributo del XML
    private String dni;
    @XmlElement(name = "nombre") // -> definimos un elemento de la clase Ejercicio1XML.Estudiante
    private String nombre;
    @XmlElement(name = "edad")
    private int edad;

    // Creamos el constructor
    public Estudiante(String dni, String nombre, int edad) {
        this.dni = dni;
        this.nombre = nombre;
        this.edad = edad;
    }

    // Creamos el constructor vácio para que se pueda crear el XML
    public Estudiante() {
    }

    // Creamos los getters y setters
    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    @Override
    public String toString() {
        return "Ejercicio1XML.Estudiante{" +
                "dni='" + dni + '\'' +
                ", nombre='" + nombre + '\'' +
                ", edad=" + edad +
                '}';
    }
}