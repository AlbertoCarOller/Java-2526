package service;

import exception.GestorException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
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

    public void cargarDatosPrueba() throws GestorException {
        // Envolvemos el entityManager creado en un try, ya que es AutoCloseable
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            // Comenzamos una transacción (SIEMPRE NECESARIA PARA MANIPULAR LOS DATOS)
            entityManager.getTransaction().begin();
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
            Coche coche2 = new Coche("5519PUF", "Ferrari", "Vendeta", 10000);
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
}
