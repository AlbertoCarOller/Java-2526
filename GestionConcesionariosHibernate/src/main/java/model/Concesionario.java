package model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

// Creamos la tabla concesionario
@Entity
public class Concesionario {
    // Esta tabla va a tener un id autogenerado (auto-incremental)
    @Id
    // Esto genera los id de forma auto-incremental, gracias a IDENTITY
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String direccion;
    /* CascadeType.ALL -> Los cambios proporcionados se pasan del padre a los hijos,
     * orphanRemoval = true -> Para eliminar de la BD los hijos que pierdan la relación con su padre
     * mappedBy = "concesionario" -> Esto indica como se llama el atributo en la otra clase, en el lado no propietario */
    // Un concesionario tiene muchos coches
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "concesionario")
    private List<Venta> ventas;

    // Un concesionario tiene muchos coches
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "concesionario")
    private List<Coche> coches;

    // Creamos el constructor vacío
    public Concesionario() {
    }

    public Concesionario(String nombre, String direccion) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.ventas = new ArrayList<>();
        this.coches = new ArrayList<>();
    }

    /**
     * Esta función va a añadir una venta a la lista
     * y va a añadirle a la venta el concesionario
     *
     * @param venta la venta a añadir
     */
    public void addVenta(Venta venta) {
        this.ventas.add(venta);
        venta.setConcesionario(this);
    }

    /**
     * Esta función va a borrar una venta de la lista
     * y va a volver a null el concesionario del objeto
     *
     * @param venta la venta a borrar
     */
    public void removeVenta(Venta venta) {
        this.ventas.remove(venta);
        venta.setConcesionario(null);
    }

    // Añadimos los helper de coches
    public void addCoche(Coche coche) {
        this.coches.add(coche);
        coche.setConcesionario(this);
    }

    public void removeCoche(Coche coche) {
        this.coches.remove(coche);
        coche.setConcesionario(null);
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public List<Venta> getVentas() {
        return ventas;
    }


}
