package service;

import exception.GestorException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import model.*;
import util.EntityManagerController;

public class GestorService {
    // El gestor va a tener la factoría de EntityManager
    private final EntityManagerFactory entityManagerFactory;

    // Creamos el constructor
    public GestorService() {
        // Creamos la conexión, se crea el objeto EntityManagerFactory
        this.entityManagerFactory = EntityManagerController.cargarEntityManagerFactory();
    }

    /* NOTA IMPORTANTE: he decidido no eliminar físicamente los coches, si son vendidos
     * tendrán un propietario, tampoco se eliminan las ventas o reparaciones, ya que en
     * una base de datos debemos de tener un histórico de este tipo de cosas, por lo tanto
     * los orphanRemoval no son necesarios ni los métodos helper de eliminación */

    /**
     * Esta función va a cargar unos datos de prueba para poder
     * empezar a trabajar y probar el programa
     *
     * @throws GestorException En caso de que haya un problema al añadir datos,
     *                         no debería de haber, ya que se borran los datos antes
     */
    public void cargarDatosPrueba() throws GestorException, PersistenceException {
        // Envolvemos el entityManager creado en un try, ya que es AutoCloseable
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            // Comenzamos una transacción (SIEMPRE NECESARIA PARA MANIPULAR LOS DATOS)
            entityManager.getTransaction().begin();
            // Borramos los datos que haya en la base de datos
            borrarDatosActuales(entityManager);
            // Creamos un un concesionario de prueba
            Concesionario concesionario = new Concesionario("Concesionario CarCity", "Calle Esperanza 12");
            // Creamos varios equipamientos
            Equipamiento equipamiento = new Equipamiento("Aire acondicionado", 202.67);
            Equipamiento equipamiento1 = new Equipamiento("Funda asientos", 79.9);
            Equipamiento equipamiento2 = new Equipamiento("Gato", 49.99);
            Equipamiento equipamiento3 = new Equipamiento("Rueda de respuesto", 80);
            // Creamos varios mecánicos
            Mecanico mecanico = new Mecanico("Chelu García", "Cambio de ruedas");
            Mecanico mecanico1 = new Mecanico("Tobio Rodríguez", "Cambio de cristales");
            Mecanico mecanico2 = new Mecanico("Respicio Godefrío", "Instalador");
            // Creamos varios propietarios
            Propietario propietario = new Propietario("98371849R", "Andrés Vega");
            Propietario propietario1 = new Propietario("17253971M", "Carla Rivas");
            // Creamos varios coches
            Coche coche = new Coche("1234ABC", "Ford", "Mustang", 10000);
            Coche coche1 = new Coche("7865ZYX", "Toyota", "F10", 3479.99);
            Coche coche2 = new Coche("5519PUF", "Ferrari", "Vendeta", 23000.23);
            // Añadimos los equipamientos a los coches
            coche.addEquipamiento(equipamiento);
            coche.addEquipamiento(equipamiento1);
            coche1.addEquipamiento(equipamiento);
            coche1.addEquipamiento(equipamiento2);
            coche2.addEquipamiento(equipamiento3);
            // Añadimos los propietarios para los coches
            propietario.addCoche(coche);
            propietario1.addCoche(coche1);
            // Añadimos al concesionario los coches
            concesionario.addCoche(coche);
            concesionario.addCoche(coche1);
            concesionario.addCoche(coche2);
            // Hacemos que los datos sean manejados/visibles, persistiendo el concesionario, los hijos también lo harán
            entityManager.persist(concesionario);
            entityManager.persist(mecanico);
            entityManager.persist(mecanico1);
            entityManager.persist(mecanico2);
            // Hacemos el commit para que definitivamente se guarden los cambios en la bd
            entityManager.getTransaction().commit();
        }
    }

    /**
     * Esta función va a eliminar los datos actuales de la base de datos,
     * es una función complementaria de la función 'cargarDatosPrueba'
     *
     * @param entityManager el EntityManager de la función 'cargarDatosPrueba'
     */
    private void borrarDatosActuales(EntityManager entityManager) throws PersistenceException {
        // Eliminamos las restricciones de foreing key (restricciones de eliminar a padres antes que a hijos)
        // createNativeQuery -> Para ejecutar sentencias SQL nativas no JPA, en este caso es necesario
        /* MUY IMPORTANTE: se hace executeUpdate porque este se utiliza no solo para insertados, borrados o
         * actualizaciones de datos, también para configuraciones, cmo esta 'SET FOREIGN_KEY_CHECKS = 0' la
         * cual está eliminando las restricciones de eliminados de las tablas, utilizamos para la configuración
         * SQL nativo porque es la única que puede tocar este tipo de configuraciones y para la eliminación
         * de las tablas utilizo JPQL */
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
        // Eliminamos los datos de todas las tablas, sin importar el orden
        entityManager.createQuery("delete from Mecanico").executeUpdate();
        entityManager.createQuery("delete from Venta").executeUpdate();
        entityManager.createQuery("delete from Equipamiento").executeUpdate();
        entityManager.createQuery("delete from Propietario").executeUpdate();
        entityManager.createQuery("delete from Coche").executeUpdate();
        entityManager.createQuery("delete from Reparacion").executeUpdate();
        entityManager.createQuery("delete from Concesionario").executeUpdate();
        // Revertimos la restricción para que esté otra vez activa
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
    }

    /**
     * Esta función da de alta un concesionario en caso de que tanto el nombre como dirección sean
     * válidos y el concesionario no esté registrado ya en la base de datos
     *
     * @param nombre    el nombre del concesionario
     * @param direccion la dirección del concesionario
     * @throws GestorException      en caso de que haya algún error con los datos o porque ya exista
     * @throws PersistenceException en caso de un error con el uso de EntityManager
     */
    public void darAltaConcesionario(String nombre, String direccion) throws GestorException, PersistenceException {
        // Comprobamos que ni el nombre ni la dirección estén vacíos
        if (nombre.isEmpty() || direccion.isEmpty()) {
            throw new GestorException("El nombre y/o dirección no son válidos");
        }
        // Creamos el EntityManager
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            /* En caso de que la lista devuelva algún concesionario, es decir que no esté vacía querrá decir que existe,
             * esto se considera si un mismo concesionario tiene un mismo nombre y dirección.
             * getResultList -> devuelve la lista de objetos que devuelve la consulta
             * createNamedQuery -> es una consulta nombrada, es decir ya existente que tiene un nombre, se le pasan
             * los parámetros necesarios, en este caso el nombre y la dirección */
            if (!entityManager.createNamedQuery("Concesionario.existeConcesionario").setParameter("nombre", nombre)
                    .setParameter("direccion", direccion).getResultList().isEmpty()) {
                throw new GestorException("La concesionario ya existe");
            }
            // En este punto no hay errores, por lo que comenzamos la transacción para la creación del concesionario
            entityManager.getTransaction().begin();
            // Creamos el concesionario
            Concesionario concesionario = new Concesionario(nombre, direccion);
            // Persistimos los datos, es decir que sea visible/controlado por Hibernate
            entityManager.persist(concesionario);
            // Guardamos los cambios en la base de datos, terminamos la transacción
            entityManager.getTransaction().commit();
        }
    }

    public void darAltaCoche(String matricula, String marca, String modelo, double precioBase) {
        // TODO: hacer la función
    }
}
