package Ejercicio2;

import jakarta.xml.bind.annotation.*;

import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "catalogo")
public class Catalogo {
    // Creamos los atributos
    @XmlAttribute(name = "consola")
    String consola;
    @XmlElementWrapper(name = "videojusgos-stock")
    @XmlElement(name = "videojuego")
    List<Videojuego> videojuegos;

    // Creamos los constructores
    public Catalogo(String consola, List<Videojuego> videojuegos) {
        this.consola = consola;
        this.videojuegos = videojuegos;
    }

    public Catalogo() {
    }

    // Creamos los getters y setters
    public String getConsola() {
        return consola;
    }

    public void setConsola(String consola) {
        this.consola = consola;
    }

    public List<Videojuego> getVideojuegos() {
        return videojuegos;
    }

    public void setVideojuegos(List<Videojuego> videojuegos) {
        this.videojuegos = videojuegos;
    }

    @Override
    public String toString() {
        return "Catalogo{" +
                "consola='" + consola + '\'' +
                ", videojuegos=" + videojuegos +
                '}';
    }
}