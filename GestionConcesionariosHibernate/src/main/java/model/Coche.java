package model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Coche {
    // El id, la primary key de la tabla va a see la matrícula
    @Id
    private String matricula;
    private String marca;
    private String modelo;
    private double precioBase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concesionario_id")
    private Concesionario concesionario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id")
    private Propietario propietario;

    // Relación 1:1 con venta, solo se pueden relaciones tablas mediante objetos completos
    @OneToOne(mappedBy = "coche")
    private Venta venta;

    /* IMPORTANTE: para unir muchos a muchos (N:M) se crea una tabla intermedia para hacer las cosas muchos
     * más fáciles, no se utiliza el JoinColum, se utiliza el JoinTable, aquí especificaremos el nombre
     * de la columna intermedia, el nombre de la fila de este lado con el que se une y el nombre de la fila
     * con la que nos unimos, es decir la inversa. La tabla dueño, es decir la que contiene el join da lo
     * mismo en la práctica cuál elegir, yo en mi caso voy a elegir esta (coche) */
    // En este caso creamos la tabla intermedia 'coche_equipamiento' para unir esta tabla y la de equipamientos
    @ManyToMany()
    @JoinTable(name = "coche_equipamiento",
            joinColumns = @JoinColumn(name = "coche_matricula"), // -> Nombre de la columna principal
            inverseJoinColumns = @JoinColumn(name = "equipamiento_id")) // Nombre de la columna de la inversa
    private List<Equipamiento> equipamientos;

    // Creamos el constructor vacío
    public Coche() {
    }

    // Creamos el constructor
    public Coche(String matricula, String marca, String modelo, double precioBase) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.precioBase = precioBase;
        this.equipamientos = new ArrayList<>();
    }

    public String getMatricula() {
        return matricula;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(double precioBase) {
        this.precioBase = precioBase;
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
