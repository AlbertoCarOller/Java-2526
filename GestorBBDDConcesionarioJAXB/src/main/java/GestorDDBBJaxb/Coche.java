package GestorDDBBJaxb;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class Coche {
    // Creamos los atributos
    @XmlAttribute(name = "id")
    private int id;
    // Creamos una variable auxiliar para dar valor al id, esto nos servirá también para recalcular el id
    protected static int idAuxiliar = 0;
    @XmlElement(name = "matricula-coche")
    private String matricula;
    @XmlElement(name = "marca")
    private String marca;
    @XmlElement(name = "modelo")
    private String modelo;
    // Si no tiene extras solo se mostrará la etiqueta de cierre
    @XmlElementWrapper(name = "equipamiento")
    @XmlElement(name = "extra")
    private List<String> equipamiento;

    // Creamos el constructor vacío
    public Coche() {
    }

    // Creamos el constructor explícito para JSON, pero que sirve para el resto, para que el id se actualice correctamente al importar
    @JsonCreator
    public Coche(@JsonProperty("matricula") String matricula,
                 @JsonProperty("marca")  String marca,
                 @JsonProperty("modelo") String modelo,
                 @JsonProperty("equipamiento") List<String> equipamiento) throws GestorBBDDJaxbExcepcion {
        if (matricula.isBlank() || marca.isBlank() || modelo.isBlank()) {
            throw new GestorBBDDJaxbExcepcion("No se ha podido crear el coche, los datos son inválidos");
        }
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.equipamiento = new ArrayList<>(equipamiento);
        id = ++this.idAuxiliar;
    }

    // Creamos los getters y setters
    public int getId() {
        return id;
    }

    // Esto es para que al crearme el objeto JSON no coga el set id para modificar el id
    @JsonIgnore
    public void setId(int id) {
        this.id = id;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public List<String> getEquipamiento() {
        return equipamiento;
    }

    // Hacemos un toString
    @Override
    public String toString() {
        return "Coche{" +
                "id=" + id +
                ", matricula='" + matricula + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", equipamiento=" + equipamiento +
                '}';
    }

    // Hacemos un equals por el campo 'matrícula'
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Coche coche = (Coche) o;
        return Objects.equals(matricula, coche.matricula);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(matricula);
    }
}
