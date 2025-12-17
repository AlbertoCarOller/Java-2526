package model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

// Se crea la tabla (entidad) departamento con el nombre 't_departamentos'
@Entity
@Table(name = "t_departamentos")
public class Departamento {

    // Para el id generamos values, gracias a IDENTITY será autoincremental
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    // --- ACTUALIZACIÓN ---
    // orphanRemoval = true: Si quito un empleado de la lista, Hibernate lo borra de la BD.
    // OneToMany -> Significa 1 a muchos, es la relación, un departamento con muchos empleados
    /* CascadeType.ALL -> Significa que si se guardan empleados en el departamento se guardará automáticamente,
     no habría que ser explícitos */
    @OneToMany(mappedBy = "departamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Empleado> empleados = new ArrayList<>();

    // Constructor vacío necesario
    public Departamento() {
    }

    // Constructor con el nombre
    public Departamento(String nombre) {
        this.nombre = nombre;
    }

    // Estas funciones nos servirán para borrar y guardar empleados en el departamento
    // Helper Method VITAL para la coherencia
    public void addEmpleado(Empleado e) {
        this.empleados.add(e); // Añadimos el empleado a la lista de de empleados del departamento
        e.setDepartamento(this); // Le asignamos el departamento al empleado
    }

    // Helper Method para borrar
    public void removeEmpleado(Empleado e) {
        this.empleados.remove(e); // Quitamos el empleado de la lista
        e.setDepartamento(null); // Le quitamos el departamento al empleado
    }

    // Getter y setters
    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String n) {
        this.nombre = n;
    }

    public List<Empleado> getEmpleados() {
        return empleados;
    }
}