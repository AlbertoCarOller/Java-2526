package model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Propietario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String dni;
    private String nombre;

    @OneToMany(mappedBy = "propietario", cascade = CascadeType.ALL)
    private List<Venta> ventas;

    @OneToMany(mappedBy = "propietario", cascade = CascadeType.ALL)
    private List<Coche> coches;

    public Propietario() {
    }

    public Propietario(String dni, String nombre) {
        this.dni = dni;
        this.nombre = nombre;
        this.ventas = new ArrayList<>();
        this.coches = new ArrayList<>();
    }

    // Creamos las funciones HELPER
    public void addVenta(Venta venta) {
        this.ventas.add(venta);
        venta.setPropietario(this);
    }

    public void addCoche(Coche coche) {
        this.coches.add(coche);
        coche.setPropietario(this);
    }

    public Long getId() {
        return id;
    }

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

    public List<Venta> getVentas() {
        return ventas;
    }

    public List<Coche> getCoches() {
        return coches;
    }
}
