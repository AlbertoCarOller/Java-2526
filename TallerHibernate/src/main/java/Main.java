import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

void main() {

    // Para obtener la factoría de entity manager
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("miUnidadPersistencia");
    // El gestor principal
    EntityManager em = emf.createEntityManager();

    try {
        // INICIAMOS UNA TRANSACCIÓN, LAS MODIFICACIONES DEBEN DE SER ATÓMICAS
        em.getTransaction().begin();
        // Creamos objetos usuario (Estado: Transient - No guardado aún)
        System.out.println("Creando usuarios en memoria...");
        Usuario usuario = new Usuario("Ana García", "ana.garcia@example.com");
        Usuario usuario1 = new Usuario("Luis Ramos", "luis.ramos@example.com");

        // Persistir (Estado: Managed - Hibernate los vigila)
        // Aquí Hibernate prepara el INSERT, pero aún no lo envía obligatoriamente a la BD
        System.out.println("Persistiendo usuarios...");
        em.persist(usuario);
        em.persist(usuario1);

        // COMMIT (confirmar cambios)
        // Aquí se ejecuta el SQL real y se cierra la transacción
        em.getTransaction().commit();

        System.out.println("¡Usuarios guardados con éxito!");
        System.out.println("ID generado para Ana: " + usuario.getId());
        System.out.println("ID generado para Luis Ramos: " + usuario1.getId());

    } catch (Exception e) {
        // Si algo falla deshacemos los cambios (Rollback)
        em.getTransaction().rollback();
        System.out.println(e.getMessage());

    } finally {
        // Cerramos los recuros
        em.close();
        emf.close();
    }
}