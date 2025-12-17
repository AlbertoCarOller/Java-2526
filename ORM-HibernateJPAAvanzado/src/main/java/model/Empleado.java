package model;

import jakarta.persistence.*;

// Tenemos la entidad, la tabla Empleado
@Entity
@Table(name = "t_empleados_adv")
// --- ACTUALIZACIÓN: Named Query ---
// Una consulta que obtiene el empleado el cual el salio esté entre el min y max indicado
@NamedQuery(
        name = "Empleado.buscarPorRangoSalarial",
        query = "SELECT e FROM Empleado e WHERE e.salario BETWEEN :min AND :max"
)
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Double salario;

    /* fetch = FetchType.LAZY -> Carga perezosa, significa que hibernate no traerá cada departamento, solo los nombres
     del empleado, etc. Pero lo traerá cuando la necesitemos, es decir cuando la utilicemos la cargará */
    @ManyToOne(fetch = FetchType.LAZY)
    /* Se carga el Departamento completo y no solo un id, un número porque cargando el objeto completo obtenemos todos
    * sus atributos, así lo hace Hibernate */
    @JoinColumn(name = "departamento_id")
    private Departamento departamento;

    public Empleado() {}

    public Empleado(String nombre, Double salario) {
        this.nombre = nombre;
        this.salario = salario;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Double getSalario() { return salario; }
    public void setSalario(Double salario) { this.salario = salario; }
    public Departamento getDepartamento() { return departamento; }
    public void setDepartamento(Departamento departamento) { this.departamento = departamento; }

    @Override
    public String toString() {
        return "Empleado{id=" + id + ", nombre='" + nombre + "', salario=" + salario + '}';
    }
}