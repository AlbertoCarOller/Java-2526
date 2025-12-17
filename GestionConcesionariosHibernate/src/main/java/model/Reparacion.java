package model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Reparacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate fecha;
    private double coste;
    private String descripcion;

    // Muchas reparaciones puede tener un coche
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coche_matricula")
    private Coche coche;

    // Muchas reparaciones pueden ser realizadas por un mecánico
    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "mecanico_id")
    private Mecanico mecanico;


    public Reparacion() {
    }

    public Reparacion(LocalDate fecha, double coste, String descripcion) {
        this.fecha = fecha;
        this.coste = coste;
        this.descripcion = descripcion;
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

    public double getCoste() {
        return coste;
    }

    public void setCoste(double coste) {
        this.coste = coste;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Coche getCoche() {
        return coche;
    }

    public void setCoche(Coche coche) {
        this.coche = coche;
    }

    public Mecanico getMecanico() {
        return mecanico;
    }

    public void setMecanico(Mecanico mecanico) {
        this.mecanico = mecanico;
    }
}
