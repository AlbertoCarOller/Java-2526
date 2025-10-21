package GestorDDBBJaxb;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "concesionario-de-coches")
public class Concesionario {
    // Creamos los atributos
    @XmlElementWrapper(name = "listado-coches")
    @XmlElement(name = "coche")
    private List<Coche> coches = new ArrayList<Coche>();

    // Creamos el constructor vacío
    public Concesionario() {}

    // Hacemos los getters y setters
    public List<Coche> getCoches() {
        return coches;
    }

    public void setCoches(List<Coche> coches) {
        this.coches = coches;
    }

    // Hacemos un toString
    @Override
    public String toString() {
        return "Concesionario{" +
                "coches=" + coches +
                '}';
    }
}
