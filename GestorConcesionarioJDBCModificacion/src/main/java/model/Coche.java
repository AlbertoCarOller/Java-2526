package model;

import java.util.ArrayList;

public class Coche {
    // Creamos los atributos
    private String matricula;
    private String marca;
    private String modelo;
    private ArrayList<String> extras;
    private double precio;
    private int idPropietario;
    private int anio;

    // Creamos el constructor
    public Coche(String matricula, String marca, String modelo, ArrayList<String> extras, double precio,
                 int idPropietario, int anio) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.extras = extras;
        this.precio = precio;
        this.idPropietario = idPropietario;
        this.anio = anio;
    }

    // Hacemos los get y set

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
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

    public ArrayList<String> getExtras() {
        return extras;
    }

    public void setExtras(ArrayList<String> extras) {
        this.extras = extras;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getIdPropietario() {
        return idPropietario;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    // Hacemos el toString
    @Override
    public String toString() {
        return "Coche{" +
                "matricula='" + matricula + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", extras=" + extras +
                ", precio=" + precio +
                ", idPropietario=" + idPropietario +
                ", anio=" + anio +
                '}';
    }
}
