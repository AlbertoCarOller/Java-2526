package model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Equipamiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private double coste;

    // La lista de quipamientos que puede tener uno o varios coches
    @ManyToMany(mappedBy = "equipamientos")
    private List<Coche> coches;

    // Creamos los constructores
    public Equipamiento() {
    }

    public Equipamiento(String nombre, double coste) {
        this.nombre = nombre;
        this.coste = coste;
        this.coches = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getCoste() {
        return coste;
    }

    public void setCoste(double coste) {
        this.coste = coste;
    }
}
