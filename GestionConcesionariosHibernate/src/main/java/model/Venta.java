package model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Venta {
    // TODO: una venta un coche, relación 1:1
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

    public Venta() {
    }

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
}
