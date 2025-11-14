package model;

public class Propietario {
    // Creamos los atributos
    private int id;
    private String dni;
    private String nombre;
    private String apellidos;
    private String telefono;

    // Creamos el constructor
    public Propietario(int id, String dni, String nombre, String apellidos, String telefono) {
        this.id = id;
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
    }

    // Creamos los get
    public int getId() {
        return id;
    }

    public String getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getTelefono() {
        return telefono;
    }

    // Creamos un toString
    @Override
    public String toString() {
        return "Propietario{" +
                "id=" + id +
                ", dni='" + dni + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", telefono='" + telefono + '\'' +
                '}';
    }
}