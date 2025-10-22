package GestorDDBBJaxb;

import jakarta.xml.bind.annotation.*;

import java.util.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "concesionario-de-coches")
public class Concesionario {
    // Creamos los atributos
    @XmlElementWrapper(name = "listado-coches")
    @XmlElement(name = "coche")
    // Creamos un conjunto para no tener que comprobar la repetición de coches
    private Set<Coche> coches = new LinkedHashSet<>();

    // Creamos el constructor vacío
    public Concesionario() {}

    // Hacemos los getters y setters
    public Set<Coche> getCoches() {
        return coches;
    }

    public void setCoches(Set<Coche> coches) {
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
