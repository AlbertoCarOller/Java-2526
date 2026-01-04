package service;

import exception.GestorException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import model.*;
import util.EntityManagerController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        // TODO: pensar bien si hacer o no las ventas en los datos de prueba
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
            if (!validarExistencia(entityManager.createNamedQuery("Concesionario.existeConcesionario").setParameter("nombre", nombre)
                    .setParameter("direccion", direccion).getResultList())) {
                throw new GestorException("El concesionario ya existe");
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

    /**
     * Esta función va a dar de alta un coche en un concesionario ya existente,
     * nos aseguramos de que los campos sean válidos y de que el concesionario
     * existe mediante su id
     *
     * @param matricula       la matrícula del coche
     * @param marca           la marca del coche
     * @param modelo          el modelo del coche
     * @param precioBase      el precio base que va a tener el coche
     * @param idConcesionario el id del concesionario a añadir el coche
     * @throws GestorException      En caso de que el concesionario no exista o algún campo del coche sea inválido
     * @throws PersistenceException en caso de que haya un error con el EntityManager
     */
    public void darAltaCoche(String matricula, String marca, String modelo, double precioBase, int idConcesionario)
            throws GestorException, PersistenceException {
        // Creamos un regex para verificar que la matrícula sea válida
        Pattern pattern = Pattern.compile("^[0-9]{4}[A-Z]{3}$");
        Matcher matcher = pattern.matcher(matricula);
        // Comprobamos si son válidos los diferentes campos del coche
        if (matricula.isEmpty() || marca.isEmpty() || modelo.isEmpty() || precioBase <= 0 || !matcher.matches()) {
            throw new GestorException("Algún campo del coche es inválido");
        }
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            // Comenzamos la transacción
            entityManager.getTransaction().begin();
            // Creamos un TypedQuery, es decir una consulta tipada para asegurarle a Java que el objeto a recibir es un Concesionario
            TypedQuery<Concesionario> typedQuery = entityManager.createNamedQuery("Concesionario.existentePorID", Concesionario.class)
                    .setParameter("id", idConcesionario);
            // Guardamos la lista de concesionario (que debe de haber 0 o 1)
            List<Concesionario> concesionarios = typedQuery.getResultList();
            // Comprobamos si existe el concesionario con el id pasado
            if (validarExistencia(concesionarios)) {
                throw new GestorException("El concesionario con id " + idConcesionario + " no existe");
            }
            // Obtenemos el concesionario
            Concesionario concesionario = concesionarios.getFirst();
            /* Agregamos el coche al concesionario, como el objeto ya (el concesionario) ya forma parte de la base
             * de datos, ya está vigilado/manegado por Hibernate y tenemos el cascade, al añadir el coche en un objeto ya
             * manejado, el coche internamente se crea también (persiste, se crea realmente cuando se hace el commit) */
            concesionario.addCoche(new Coche(matricula, marca, modelo, precioBase));
            // Terminamos la transacción
            entityManager.getTransaction().commit();
        }
    }

    /**
     * Esta función va a añadir un equipamiento existente a un coche por
     * su matrícula en caso de que exista el coche y el equipamiento
     *
     * @param matriculaCoche la matrícula del coche a añadir el equipamiento
     * @param idEquipamiento el id del equipamiento a añadir
     * @return el precio del coche con los extras
     * @throws GestorException      en caso de que no se encuentre el coche o el equipamiento
     * @throws PersistenceException en caso de que haya algún error con EntityManager
     */
    public double instalarExtra(String matriculaCoche, int idEquipamiento) throws GestorException, PersistenceException {
        double precioFinal;
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            // Comenzamos la transacción
            entityManager.getTransaction().begin();
            List<Coche> coches = obtenerListaCoches(matriculaCoche, entityManager);
            // En caso de que el coche no esté lanzamos excepción
            if (validarExistencia(coches)) {
                throw new GestorException("El coche no existe");
            }
            Coche coche = coches.getFirst();
            // Comprobamos que exista el equipamiento
            TypedQuery<Equipamiento> equipamientoTypedQuery =
                    entityManager.createQuery("select e from Equipamiento e where e.id = :idEquipamiento", Equipamiento.class)
                            .setParameter("idEquipamiento", idEquipamiento);
            // Obtenemos el equipamiento
            List<Equipamiento> equipamientos = equipamientoTypedQuery.getResultList();
            // Comprobamos si existe el equipamiento
            if (validarExistencia(equipamientos)) {
                throw new GestorException("El equipamiento no existe");
            }
            Equipamiento equipamiento = equipamientos.getFirst();
            // Se añade el equipamiento al coche
            coche.addEquipamiento(equipamiento);
            // Se hace commit, termina la transacción
            entityManager.getTransaction().commit();
            // Se le asigna el precio final del coche con los extras a la varible
            precioFinal = calcularPrecioTotal(coche);
        }
        // Devolvemos el precio final del coche
        return precioFinal;
    }

    /**
     * Esta función va a calcular el precio del coche con los extras añadidos
     *
     * @param coche el coche del que se calcula el precio nuevo
     * @return un double que representa el valor total
     */
    private double calcularPrecioTotal(Coche coche) {
        return coche.getPrecioBase() + coche.getEquipamientos().stream().mapToDouble(Equipamiento::getCoste).sum();
    }

    /**
     * Esta función va a crear una reparación la cual se asociará a
     * un coche y un mecánico
     *
     * @param matriculaCoche   la matrícula del coche a realizar la reparación
     * @param idMecanico       el id del mecánico que va a realizar la reparación
     * @param fecha            la fecha en la que se va a realizar la reparación
     * @param costeInversion   el coste de la reparación
     * @param breveDescripcion la descripción de la reparación
     * @throws GestorException        en caso de que haya algún dato incorrecto
     * @throws PersistenceException   en caso de un error con EntityManager
     * @throws DateTimeParseException en caso de que al intentar parsear la fecha (String) a LocalDate
     */
    public void registrarReparacion(String matriculaCoche, int idMecanico, String fecha, double costeInversion,
                                    String breveDescripcion) throws GestorException, PersistenceException, DateTimeParseException {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            // Comenzamos la transacción
            entityManager.getTransaction().begin();
            // Comprobamos el coste de inversión y la breve descripción
            if (costeInversion <= 0 || breveDescripcion.isEmpty()) {
                throw new GestorException("El coste de inversión o la descripción son inválidos");
            }
            /* Creamos una variable donde almacenar la fecha como LocalDate parseándola,
             * utilizamos el DateTimeFormatter.ofPattern() para que el formato esté en fecha de España */
            LocalDate fechaReal = LocalDate.parse(fecha, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            // Comprobamos que la fecha del registro no sea posterior a la actual
            if (fechaReal.isAfter(LocalDate.now())) {
                throw new GestorException("La fecha del registro es posterior a la actual");
            }
            List<Coche> coches = obtenerListaCoches(matriculaCoche, entityManager);
            // En caso de que no exista el coche lanzamos excepción
            if (validarExistencia(coches)) {
                throw new GestorException("El coche no existe");
            }
            Coche coche = coches.getFirst();
            // Obtenemos el mecánico mediante el id
            TypedQuery<Mecanico> mecanicoTypedQuery = entityManager
                    .createQuery("select m from Mecanico m where m.id = :idMecanico", Mecanico.class)
                    .setParameter("idMecanico", idMecanico);
            List<Mecanico> mecanicos = mecanicoTypedQuery.getResultList();
            // En caso de que no exista el mecánico lanzamos excepción
            if (validarExistencia(mecanicos)) {
                throw new GestorException("El mecánico no existe");
            }
            Mecanico mecanico = mecanicos.getFirst();
            // Creamos la reparación una vez que tenemos todos los datos
            Reparacion reparacion = new Reparacion(fechaReal, costeInversion, breveDescripcion);
            // Añadimos la reparación al mecánico
            mecanico.addReparacion(reparacion);
            // Añadimos la reparación al coche
            coche.addReparacion(reparacion);
            // Terminamos la transacción
            entityManager.getTransaction().commit();
        }
    }

    /**
     * Esta función va a vender el coche a un propietario en caso de que el coche no esté
     * vendido y exista, creando así una venta
     *
     * @param dni             el dni del propietario
     * @param nombre          el nombre del propietario
     * @param matriculaCoche  la matrícula del coche a vender
     * @param idConcesionario el id del concesionario en el que se va a realizar la venta
     * @param precioPactado   el precio pactado de la venta
     * @throws GestorException      en caso de que el coche, el concesionario o algún campo sea incorrecto
     * @throws PersistenceException en caso de un error con EntityManager
     */
    public void venderCoche(String dni, String nombre, String matriculaCoche, int idConcesionario, double precioPactado)
            throws GestorException, PersistenceException {
        // Creamos un Pattern para validar el formato del dni pasado
        Pattern pattern = Pattern.compile("^[0-9]{8}([A-Z]|[a-z])$");
        Matcher matcher = pattern.matcher(dni);
        // Comprobamos si el formato del dni y el nombre son válidos
        if (!matcher.matches() || nombre.isEmpty()) {
            throw new GestorException("El nombre y/o la matrícula no son válidos");
        }
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            // Comenzamos la transacción
            entityManager.getTransaction().begin();
            // Vamos a obtener el concesionario por su id en caso de que exista
            TypedQuery<Concesionario> concesionarioTypedQuery =
                    entityManager.createNamedQuery("Concesionario.existentePorID", Concesionario.class)
                            .setParameter("id", idConcesionario);
            List<Concesionario> concesionarios = concesionarioTypedQuery.getResultList();
            // En caso de que no existe el concesionario lanzamos excepción
            if (validarExistencia(concesionarios)) {
                throw new GestorException("El concesionario no existe");
            }
            Concesionario concesionario = concesionarios.getFirst();
            List<Coche> coches = obtenerListaCoches(matriculaCoche, entityManager);
            if (validarExistencia(coches)) {
                throw new GestorException("El coche no existe");
            }
            Coche coche = coches.getFirst();
            // Vamos a comprobar si el coche ya ha sido vendido
            /*if (coche.getPropietario() != null) {
                throw new GestorException("El coche ya ha sido vendido antes");
            }*/
            // Vamos a intentar obtener el propietario en caso de que exista ya en la base de datos porque ya haya comprado antes
            TypedQuery<Propietario> propietarioTypedQuery = entityManager
                    .createQuery("select p from Propietario p where p.dni like :dni", Propietario.class)
                    .setParameter("dni", dni);
            List<Propietario> propietarios = propietarioTypedQuery.getResultList();
            Propietario propietario;
            // En caso de que no haya propietario, se crea
            if (validarExistencia(propietarios)) {
                propietario = new Propietario(dni, nombre);
                // Persistimos el propietario
                entityManager.persist(propietario);
            }
            propietario = propietarios.getFirst();
            // Le añadimos el coche al propietario
            propietario.addCoche(coche);
            // Creamos la venta
            Venta venta = new Venta(LocalDate.now(), precioPactado);
            // Añadimos el coche a la venta
            venta.setCoche(coche);
            // Le añadimos al propietario la venta
            propietario.addVenta(venta);
            // Le añadimos al concesionario la venta
            concesionario.addVenta(venta);
            // Terminamos la transacción
            entityManager.getTransaction().commit();
        }
    }

    /**
     * Esta función va a comprobar que exista o no
     * el objeto de una comprobación obtenida por
     * una TypedQuery, '?' quiere decir que da igual
     * el contenido de la lista, el tipo que sea, ya
     * que en este caso solo me importa si está vacía o no
     *
     * @param lista la lista a comprobar
     * @return si está vacía o no
     */
    private boolean validarExistencia(List<?> lista) {
        return lista.isEmpty();
    }

    /**
     * Esta función va a devolver una lista el coche
     * devuelto por la TypedQuery, este busca un coche
     * por su matrícula
     *
     * @param matriculaCoche la matrícula del coche a obtener
     * @param entityManager  el EntityManager de la transacción
     * @return la lista de coches (solo debería de haber 1 o 0)
     * @throws PersistenceException en caso de que haya algún error con la TypedQuery
     */
    private List<Coche> obtenerListaCoches(String matriculaCoche, EntityManager entityManager) throws PersistenceException {
        // Vamos a obtener el coche por su matrícula en caso de que exista
        TypedQuery<Coche> cocheTypedQuery = entityManager
                .createNamedQuery("Coche.obtenerPorMatricula", Coche.class)
                .setParameter("matriculaCoche", matriculaCoche);
        return cocheTypedQuery.getResultList();
    }
}