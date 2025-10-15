package Ejercicio2;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Videojuego {
    // Creamos los atributos
    @XmlAttribute(name = "id")
    int id;
    @XmlElement(name = "titulo")
    String titulo;
    @XmlElement(name = "company")
    String company;
    @XmlElement(name = "pegi")
    int pegi;

    // Creamos los constructores
    public Videojuego(int id, String titulo, String company, int pegi) {
        this.id = id;
        this.titulo = titulo;
        this.company = company;
        this.pegi = pegi;
    }

    public Videojuego() {
    }

    // Creamos los getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public int getPegi() {
        return pegi;
    }

    public void setPegi(int pegi) {
        this.pegi = pegi;
    }

    @Override
    public String toString() {
        return "Videojuego{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", company='" + company + '\'' +
                ", pegi=" + pegi +
                '}';
    }
}
