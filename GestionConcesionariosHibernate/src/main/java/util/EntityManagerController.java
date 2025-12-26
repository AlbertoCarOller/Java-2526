package util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;

public class EntityManagerController {

    /**
     * Esta función va a intentar crear el EntityManagerFactory para la posterior creación de
     * EntityManagers, en caso de que haya un problema propagamos la excepción
     *
     * @return El EntityManagerFactory
     * @throws PersistenceException en caso de que haya un problema con su creación
     */
    public static EntityManagerFactory cargarEntityManagerFactory() throws PersistenceException {
        // Se intenta crear el EntityManagerFactory con la configuración del persistance.xml
        return Persistence.createEntityManagerFactory("miUnidadPersistencia");
    }

    /**
     * Esta función va a cerrar el EntityManagerFactory, esto se hace para evitar
     * que queden conexiones abiertas un tiempo por el lado del servidor incluso
     * después del fin de la ejecución del programa
     *
     * @param emf el EntityManagerFactory a cerrar
     */
    public static void cerrarEntityManagerFactory(EntityManagerFactory emf) {
        // Cerramos el recurso EntityManagerFactory
        emf.close();
    }
}
