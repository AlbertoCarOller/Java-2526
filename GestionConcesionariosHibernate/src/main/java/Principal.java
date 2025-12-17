import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import model.Concesionario;
import model.Venta;

void main() {
    try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("miUnidadPersistencia")) {
        EntityManager em = emf.createEntityManager();
        // Creamos las objetos, el concesionario y dos ventas solo para probar
        // Se crea transacción
        em.getTransaction().begin();
        Concesionario concesionario = new Concesionario("Concesionario", "Dirección2234");
        Venta venta1 = new Venta(LocalDate.now(), 1200);
        Venta venta2 = new Venta(LocalDate.now(), 1945);
        // Añadimos las ventas
        concesionario.getVentas().add(venta1);
        concesionario.getVentas().add(venta2);
        // HASTA QUE NO HAGAMOS EL persist NO ESTARÁ CONTROLADO, MANEJADO POR HIBERNATE, AL HACER ESTO HIBERNATE ESTARÁ PENDIENTE A SUS CAMBIOS
        em.persist(concesionario); // Persistimos el concesionario, como tenemos cascade.ALL afecta a los hijos de este
        em.getTransaction().commit(); // Hacemos un commit en caso de que no haya excepción
    }
}
