import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import model.Departamento;
import model.Empleado;

public class MainAsociaciones {
    public static void main(String[] args) {
        /* EntityManagerFactory -> Es una fábrica que permite crear trabajadores temporales (EntityManager)
         * que son los que nos permiten manejar las funciones principales de hibernate como: persist(), find(), remove(), etc.
         * Debemos de pasarle el nombre de nuestra unidad de persistencia (persistance.xml) donde guardamos la configuración
         * de nuestro usuario, contraseña y demás */
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("miUnidadPersistencia");
        // Trabajador individual para manipular los datos
        EntityManager em = emf.createEntityManager();

        // Comenzamos una transacción
        em.getTransaction().begin();

        System.out.println("--- PRUEBA 1: Cascade Persist ---");
        // Escenario: Creamos un departamento y empleados en memoria
        Departamento d = new Departamento("Marketing");
        Empleado e1 = new Empleado("Marta", 3000.0);
        Empleado e2 = new Empleado("Jorge", 2500.0);

        // Usamos el helper method (Mantiene coherencia Java), añadimos dos empleados al departamento
        d.addEmpleado(e1);
        d.addEmpleado(e2);

        // Solo guardamos el Padre. Gracias a CascadeType. ALL, los hijos se guardan.
        em.persist(d); // -> Con esto el objeto, es decir el departamento pasa a estar gestionado, el em estará pendiente a sus cambios

        em.getTransaction().commit();
        System.out.println("Departamento guardado con ID: " + d.getId());

        // ----------------------------------------------------

        System.out.println("\n--- PRUEBA 2: Orphan Removal ---");
        em.getTransaction().begin();

        Departamento dFound = em.find(Departamento.class, d.getId());

        // Eliminamos al primer empleado de la LISTA JAVA
        // Gracias a orphanRemoval=true, esto lanzará un DELETE SQL
        System.out.println("Eliminando empleado de la lista...");
        dFound.getEmpleados().remove(0);

        em.getTransaction().commit();
        System.out.println("Empleado eliminado de BBDD al sacarlo de la lista.");

        em.close();
        emf.close();
    }
}