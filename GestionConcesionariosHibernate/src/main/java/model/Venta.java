package model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate fecha;
    private double precioFinal;

    // Creamos la relación con carga perezosa (muchos coches pertenecen a un concesionario)
    @ManyToOne(fetch = FetchType.LAZY)
    // El joinColum solo se pone en la tabla que tiene un campo que viene de otra tabla (foreingKey)
    @JoinColumn(name = "concesionario_id")
    private Concesionario concesionario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id")
    private Propietario propietario;

    // Relación 1:1 con Coche
    @OneToOne(fetch = FetchType.LAZY)
    // Con unique = true nos aseguramos que en la tabla ventas el coche sea único
    @JoinColumn(name = "coche_matricula", unique = true)
    private Coche coche;

    public Venta() {
    }

    /* IMPORTANTE: al constructor no se le pasan los objetos ya que estos forman parte de la relación y esta
     se va creando poco */
    public Venta(LocalDate fecha, double precioFinal) {
        this.fecha = fecha;
        this.precioFinal = precioFinal;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public double getPrecioFinal() {
        return precioFinal;
    }

    public void setPrecioFinal(double precioFinal) {
        this.precioFinal = precioFinal;
    }

    public Concesionario getConcesionario() {
        return concesionario;
    }

    public void setConcesionario(Concesionario concesionario) {
        this.concesionario = concesionario;
    }

    public Propietario getPropietario() {
        return propietario;
    }

    public void setPropietario(Propietario propietario) {
        this.propietario = propietario;
    }

    public Coche getCoche() {
        return coche;
    }

    public void setCoche(Coche coche) {
        this.coche = coche;
    }

    @Override
    public String toString() {
        return "Venta{" +
                "id=" + id +
                ", fecha=" + fecha +
                ", precioFinal=" + precioFinal +
                ", concesionario=" + concesionario.getId() +
                ", propietario=" + propietario.getDni() +
                ", coche=" + coche.getMatricula() +
                '}';
    }
}
