package model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Mecanico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String especialidad;

    // Un mecánico hace varias reparaciones
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "mecanico")
    private List<Reparacion> reparaciones;

    // Creamos los constructores
    public Mecanico() {
    }

    public Mecanico(String nombre, String especialidad) {
        this.nombre = nombre;
        this.especialidad = especialidad;
    }

    // Creamos las funciones de ayuda
    public void addReparacion(Reparacion reparacion) {
        reparaciones.add(reparacion);
        reparacion.setMecanico(this);
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

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public List<Reparacion> getReparaciones() {
        return reparaciones;
    }

    @Override
    public String toString() {
        return "Mecanico{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", especialidad='" + especialidad + '\'' +
                '}';
    }
}
