import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import model.Empleado;

public class MainCicloVida {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("miUnidadPersistencia");
        EntityManager em = emf.createEntityManager();


        System.out.println("--- PRUEBA 3: Dirty Checking ---");
        em.getTransaction().begin();

        // 1. Crear dato inicial
        Empleado emp = new Empleado("Carlos", 1000.0);
        em.persist(emp);
        em.getTransaction().commit(); // Se guarda en BD y emp sigue Managed

        // 2. Modificar sin save
        em.getTransaction().begin();
        emp.setSalario(5000.0); // Modificación en objeto Managed
        System.out.println("Cambiando salario en memoria...");
        em.getTransaction().commit(); // Hibernate detecta cambio -> Update automático

        em.close();

        System.out.println("\n--- PRUEBA 4: Error de Merge ---");
        // 'emp' ahora está DETACHED
        emp.setNombre("Carlos Detached"); // Cambio en objeto desconectado

        EntityManager em2= emf.createEntityManager();
        em2.getTransaction().begin();

        // MERGE devuelve una COPIA gestionada. El original 'emp' se ignora.
        Empleado empGestionado = em2.merge(emp);

        // Si modifico 'emp' (detached), NO SIRVE
        emp.setSalario(999.0);

        // Si modifico 'empGestionado' (managed), SI SIRVE
        empGestionado.setSalario(8888.0);

        em2.getTransaction().commit();
        em2.close();

        emf.close();
    }
}

