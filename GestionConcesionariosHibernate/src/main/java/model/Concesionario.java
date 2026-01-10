package model;

import exception.GestorException;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Creamos la tabla concesionario
@Entity
// Creamos una consulta nombrada para ver si existe el concesionario por el nombre y dirección
// Importante en JPQL al trabajar con objetos hay que ponerles alias es como crear el nombre de la variable
@NamedQuery(
        name = "Concesionario.existeConcesionario",
        query = "select c from Concesionario c where c.nombre like :nombre and" +
                " c.direccion like :direccion"
)
// Creamos una consulta en la que seleccionamos un concesionario por su id
@NamedQuery(
        name = "Concesionario.existentePorID",
        query = "select c from Concesionario c where c.id = :id"
)
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "concesionario")
    private List<Venta> ventas;

    // Un concesionario tiene muchos coches
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "concesionario")
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

    // Añadimos los helper de coches
    public void addCoche(Coche coche) throws GestorException {
        if (this.coches.contains(coche)) {
            throw new GestorException("El coche ya está en el concesionario");
        }
        this.coches.add(coche);
        coche.setConcesionario(this);
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

    public List<Coche> getCoches() {
        return coches;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Concesionario that = (Concesionario) o;
        return Objects.equals(nombre, that.nombre) && Objects.equals(direccion, that.direccion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre, direccion);
    }

    @Override
    public String toString() {
        return "Concesionario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", ventas=" + ventas +
                ", coches=" + coches.stream().map(Coche::getMatricula).toList() +
                '}';
    }
}
