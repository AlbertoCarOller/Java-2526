import jakarta.persistence.*;

// Le decimos que es una tabla con 'Entity'
@Entity
//@Table(name = "usuario_t") -> Para indicarle un nombre personalizado a la tabla
public class Usuario {
    // Creamos el atributo principal (Primary Key) con 'id'
    @Id
    // Esta propiedad IDENTITY hace que los id se generen automáticamente (auto-incremental)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Creamos los atributos
    // Crea una columna para indicarle que el id es una columna y se va a llamar así
    @Column(name = "user_id", nullable = false, unique = true)
    private Long id;
    // No puede haber nulos y la longitud es de 100
    @Column(length = 100, nullable = false, name = "nombre_completo")
    private String nombre;
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    // Creamos el construcor vacío
    public Usuario() {
    }

    // Constructor completo
    public Usuario(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }

    // Creamos los get
    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    // Creamos el toString
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
