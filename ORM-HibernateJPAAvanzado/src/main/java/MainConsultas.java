
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import model.Departamento;
import model.Empleado;

import java.util.List;

public class MainConsultas {
    public static void main(String[] args) {
        generarDatos();

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("miUnidadPersistencia");
        EntityManager em = emf.createEntityManager();

        System.out.println("--- PRUEBA 5: Named Queries ---");
        // Llamamos a la query que creamos anteriormente en Empleado
        TypedQuery<Empleado> query = em.createNamedQuery("Empleado.buscarPorRangoSalarial", Empleado.class);
        // Le pasamos los argumentos correspondientes que necesitaba la query
        query.setParameter("min", 1000.0);
        query.setParameter("max", 2000.0);

        // Guardamos en una lista los empleados devueltos por la consulta
        List<Empleado> resultados = query.getResultList();
        System.out.println("Empleados encontrados: " + resultados.size());

        System.out.println("\n--- PRUEBA 6: LazyInit & JOIN FETCH ---");
        // Consulta OPTIMIZADA: Trae Departamento + Empleados en 1 viaje
        // Evita el N+1 y evita LazyInitializationException
        String jpql = "SELECT d FROM Departamento d JOIN FETCH d.empleados";

        List<Departamento> deps = em.createQuery(jpql, Departamento.class).getResultList();

        em.close(); // Cerramos sesión

        // Podemos acceder a la lista aunque la sesión esté cerrada
        for (Departamento d : deps) {
            System.out.println("Departamento: " + d.getNombre());
            System.out.println(" - Empleados: " + d.getEmpleados().size());
        }

        emf.close();
    }

    private static void generarDatos() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("miUnidadPersistencia");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Departamento d = new Departamento("Logística");
        d.addEmpleado(new Empleado("Pepe", 1200.0));
        d.addEmpleado(new Empleado("Lucia", 1800.0));
        em.persist(d);
        em.getTransaction().commit();
        em.close();
    }
}