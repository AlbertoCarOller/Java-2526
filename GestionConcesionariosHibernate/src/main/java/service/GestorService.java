package service;

import exception.GestorException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import model.*;
import util.ConfigLoader;
import util.EntityManagerController;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GestorService {
    // El gestor va a tener la factoría de EntityManager
    private final EntityManagerFactory entityManagerFactory;
    private final Properties prop;

    // Creamos el constructor
    public GestorService() throws IOException, PersistenceException {
        // Creamos la conexión, se crea el objeto EntityManagerFactory
        this.entityManagerFactory = EntityManagerController.cargarEntityManagerFactory();
        // Inicializamos el properties
        prop = new Properties();
        // Cargamos los datos en el properties
        ConfigLoader.cargarPropierties(prop);
    }

    /* NOTA IMPORTANTE: he decidido no eliminar físicamente los coches, si son vendidos
     * tendrán un propietario, tampoco se eliminan las ventas o reparaciones, ya que en
     * una base de datos debemos de tener un histórico de este tipo de cosas, por lo tanto
     * los orphanRemoval no son necesarios ni los métodos helper de eliminación */

    /**
     * CORRECTO
     * Esta función va a cargar unos datos de prueba para poder
     * empezar a trabajar y probar el programa
     *
     * @throws GestorException En caso de que haya un problema al añadir datos,
     *                         no debería de haber, ya que se borran los datos antes
     *                         o con el EntityManager
     */
    public void cargarDatosPrueba() throws GestorException {
        // TODO: preguntar si hacer o no las ventas en los datos de prueba
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            // Comenzamos una transacción (SIEMPRE NECESARIA PARA MANIPULAR LOS DATOS)
            entityManager.getTransaction().begin();
            // Borramos los datos que haya en la base de datos
            borrarDatosActuales(entityManager);
            // Creamos un un concesionario de prueba
            Concesionario concesionario = new Concesionario("Concesionario CarCity".toUpperCase(), "Calle Esperanza 12".toUpperCase());
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
            Coche coche3 = new Coche("6666DEM", "Dacia", "Duster", 4020.54);
            // Creamos las ventas para el 'coche' y 'coche1'
            Venta venta = new Venta(LocalDate.now(), 6500);
            coche.setVenta(venta);
            venta.setCoche(coche);
            concesionario.addVenta(venta);
            propietario.addVenta(venta);
            //-------
            Venta venta1 = new Venta(LocalDate.now(), 2500.67);
            coche1.setVenta(venta1);
            venta1.setCoche(coche1);
            concesionario.addVenta(venta1);
            propietario1.addVenta(venta1);
            // Persistimos los coches
            entityManager.persist(coche);
            entityManager.persist(coche1);
            entityManager.persist(coche2);
            entityManager.persist(coche3);
            // Persistimos los equipamientos
            entityManager.persist(equipamiento);
            entityManager.persist(equipamiento1);
            entityManager.persist(equipamiento2);
            entityManager.persist(equipamiento3);
            // Persistimos los propietarios
            entityManager.persist(propietario);
            entityManager.persist(propietario1);
            // Añadimos los equipamientos a los coches
            coche.addEquipamiento(equipamiento);
            coche.addEquipamiento(equipamiento1);
            coche1.addEquipamiento(equipamiento);
            coche1.addEquipamiento(equipamiento2);
            coche.addEquipamiento(equipamiento3);
            // Añadimos los propietarios para los coches
            propietario.addCoche(coche);
            propietario1.addCoche(coche1);
            // Añadimos al concesionario los coches
            concesionario.addCoche(coche2);
            concesionario.addCoche(coche3);
            // Hacemos que los datos sean manejados/visibles, persistiendo el concesionario, los hijos también lo harán
            entityManager.persist(concesionario);
            entityManager.persist(mecanico);
            entityManager.persist(mecanico1);
            entityManager.persist(mecanico2);
            // Hacemos el commit para que definitivamente se guarden los cambios en la bd
            entityManager.getTransaction().commit();
        } catch (GestorException | PersistenceException e) {
            // En caso de que sea distinto de null se cierra
            if (entityManager != null) {
                entityManager.getTransaction().rollback();
            }
            throw new GestorException(e.getMessage());
        } finally {
            // En caso de que sea distinto de null cerramos
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    /**
     * CORRECTO
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
        //entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
        // Eliminamos los datos de todas las tablas, sin importar el orden
        // Modificación, eliminamos teniendo en cuenta el orden para no tener que utilizar las NativeQuery
        entityManager.createQuery("delete from Venta ").executeUpdate();
        entityManager.createQuery("delete from Reparacion ").executeUpdate();
        entityManager.createQuery("delete from Equipamiento ").executeUpdate();
        entityManager.createQuery("delete from Coche ").executeUpdate();
        entityManager.createQuery("delete from Propietario ").executeUpdate();
        entityManager.createQuery("delete from Concesionario ").executeUpdate();
        entityManager.createQuery("delete from Mecanico").executeUpdate();
        // Revertimos la restricción para que esté otra vez activa
        //entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
    }

    /**
     * CORRECTO
     * Esta función da de alta un concesionario en caso de que tanto el nombre como dirección sean
     * válidos y el concesionario no esté registrado ya en la base de datos
     *
     * @param nombre    el nombre del concesionario
     * @param direccion la dirección del concesionario
     * @throws GestorException en caso de que haya algún error con los datos o porque ya exista
     *                         o en caso de un error con el uso de EntityManager
     */
    public void darAltaConcesionario(String nombre, String direccion) throws GestorException {
        // Comprobamos que ni el nombre ni la dirección estén vacíos
        if (nombre.isEmpty() || direccion.isEmpty()) {
            throw new GestorException("El nombre y/o dirección no son válidos");
        }
        // Creamos el EntityManager
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            /* En caso de que la lista devuelva algún concesionario, es decir que no esté vacía querrá decir que existe,
             * esto se considera si un mismo concesionario tiene un mismo nombre y dirección.
             * getResultList -> devuelve la lista de objetos que devuelve la consulta
             * createNamedQuery -> es una consulta nombrada, es decir ya existente que tiene un nombre, se le pasan
             * los parámetros necesarios, en este caso el nombre y la dirección */
            if (!validarExistencia(entityManager.createNamedQuery("Concesionario.existeConcesionario")
                    .setParameter("nombre", nombre.toUpperCase())
                    .setParameter("direccion", direccion.toUpperCase()).getResultList())) {
                throw new GestorException("El concesionario ya existe");
            }
            // Creamos el concesionario
            Concesionario concesionario = new Concesionario(nombre.toUpperCase(), direccion.toUpperCase());
            // Persistimos los datos, es decir que sea visible/controlado por Hibernate
            entityManager.persist(concesionario);
            // Guardamos los cambios en la base de datos, terminamos la transacción
            entityManager.getTransaction().commit();
        } catch (GestorException | PersistenceException e) {
            if (entityManager != null) {
                entityManager.getTransaction().rollback();
            }
            throw new GestorException(e.getMessage());
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    /**
     * CORRECTO
     * Esta función va a dar de alta un coche en un concesionario ya existente,
     * nos aseguramos de que los campos sean válidos y de que el concesionario
     * existe mediante su id
     *
     * @param matricula       la matrícula del coche
     * @param marca           la marca del coche
     * @param modelo          el modelo del coche
     * @param precioBase      el precio base que va a tener el coche
     * @param idConcesionario el id del concesionario a añadir el coche
     * @throws GestorException En caso de que el concesionario no exista o algún campo del coche sea inválido
     *                         o en caso de que haya un error con el EntityManager
     */
    public void darAltaCoche(String matricula, String marca, String modelo, double precioBase, long idConcesionario)
            throws GestorException {
        // Comprobamos si son válidos los diferentes campos del coche
        if (marca.isEmpty() || modelo.isEmpty() || precioBase <= 0 || !matricula.matches("^[0-9]{4}[A-Z]{3}$")) {
            throw new GestorException("Algún campo del coche es inválido");
        }
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            // Comenzamos la transacción
            entityManager.getTransaction().begin();
            Concesionario concesionario = obtenerConcesionario(idConcesionario, entityManager);
            // Comprobamos si existe el concesionario con el id pasado
            if (concesionario == null) {
                throw new GestorException("El concesionario con id " + idConcesionario + " no existe");
            }
            Coche coche = obtenerCoche(matricula.toUpperCase(), entityManager);
            // Validamos si existe un coche con esa matrícula en general en la bd
            if (coche != null) {
                throw new GestorException("El coche ya existe");
            }
            /* Agregamos el coche al concesionario, como el objeto ya (el concesionario) ya forma parte de la base
             * de datos, ya está vigilado/manegado por Hibernate y tenemos el cascade, al añadir el coche en un objeto ya
             * manejado, el coche internamente se crea también (persiste, se crea realmente cuando se hace el commit) */
            // Ahora sí que tenemos que persistir el coche al haber quitado el cascade.ALL
            coche = new Coche(matricula.toUpperCase(), marca, modelo, precioBase);
            // Persistimos
            entityManager.persist(coche);
            // Añadimos el coche
            concesionario.addCoche(coche);
            // Terminamos la transacción
            entityManager.getTransaction().commit();
        } catch (GestorException | PersistenceException e) {
            if (entityManager != null) {
                entityManager.getTransaction().rollback();
            }
            throw new GestorException(e.getMessage());
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    /**
     * CORRECTO
     * Esta función va a añadir un equipamiento existente a un coche por
     * su matrícula en caso de que exista el coche y el equipamiento
     *
     * @param matriculaCoche la matrícula del coche a añadir el equipamiento
     * @param idEquipamiento el id del equipamiento a añadir
     * @return el precio del coche con los extras
     * @throws GestorException en caso de que no se encuentre el coche o el equipamiento
     *                         o en caso de que haya algún error con EntityManager
     */
    public double instalarExtra(String matriculaCoche, long idEquipamiento) throws GestorException {
        double precioFinal;
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            // Comenzamos la transacción
            entityManager.getTransaction().begin();
            model.Coche coche = obtenerCoche(matriculaCoche.toUpperCase(), entityManager);
            // En caso de que el coche no esté lanzamos excepción
            if (coche == null) {
                throw new GestorException("El coche no existe");
            }
            // Obtenemos el equipamiento
            Equipamiento equipamiento = obtenerEquipamiento(idEquipamiento, entityManager);
            // Comprobamos si existe el equipamiento
            if (equipamiento == null) {
                throw new GestorException("El equipamiento no existe");
            }
            // Se añade el equipamiento al coche
            coche.addEquipamiento(equipamiento);
            // Se hace commit, termina la transacción
            entityManager.getTransaction().commit();
            // Se le asigna el precio final del coche con los extras a la varible
            precioFinal = coche.calcularPrecioTotal();
        } catch (GestorException | PersistenceException e) {
            if (entityManager != null) {
                entityManager.getTransaction().rollback();
            }
            throw new GestorException(e.getMessage());
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        // Devolvemos el precio final del coche
        return precioFinal;
    }

    /**
     * CORRECTO
     * Esta función va a crear una reparación la cual se asociará a
     * un coche y un mecánico
     *
     * @param matriculaCoche   la matrícula del coche a realizar la reparación
     * @param idMecanico       el id del mecánico que va a realizar la reparación
     * @param fecha            la fecha en la que se va a realizar la reparación
     * @param costeInversion   el coste de la reparación
     * @param breveDescripcion la descripción de la reparación
     * @throws GestorException        en caso de que haya algún dato incorrecto
     *                                o en caso de un error con EntityManager
     * @throws DateTimeParseException en caso de que al intentar parsear la fecha (String) a LocalDate
     */
    public void registrarReparacion(String matriculaCoche, long idMecanico, String fecha, double costeInversion,
                                    String breveDescripcion) throws GestorException, DateTimeParseException {
        /* Creamos una variable donde almacenar la fecha como LocalDate parseándola,
         * utilizamos el DateTimeFormatter.ofPattern() para que el formato esté en fecha de España */
        LocalDate fechaReal = LocalDate.parse(fecha, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            // Comenzamos la transacción
            entityManager.getTransaction().begin();
            // Comprobamos el coste de inversión y la breve descripción
            if (costeInversion <= 0 || breveDescripcion.isEmpty()) {
                throw new GestorException("El coste de inversión o la descripción son inválidos");
            }
            // Comprobamos que la fecha del registro no sea posterior a la actual
            if (fechaReal.isAfter(LocalDate.now())) {
                throw new GestorException("La fecha del registro es posterior a la actual");
            }
            Coche coche = obtenerCoche(matriculaCoche.toUpperCase(), entityManager);
            // En caso de que no exista el coche lanzamos excepción
            if (coche == null) {
                throw new GestorException("El coche no existe");
            }
            // Obtenemos el mecánico mediante el id
            Mecanico mecanico = obtenerMecanico(idMecanico, entityManager);
            // En caso de que no exista el mecánico lanzamos excepción
            if (mecanico == null) {
                throw new GestorException("El mecánico no existe");
            }
            // Creamos la reparación una vez que tenemos todos los datos
            Reparacion reparacion = new Reparacion(fechaReal, costeInversion, breveDescripcion);
            // Añadimos la reparación al mecánico
            mecanico.addReparacion(reparacion);
            // Añadimos la reparación al coche
            coche.addReparacion(reparacion);
            // Terminamos la transacción
            entityManager.getTransaction().commit();
        } catch (PersistenceException | GestorException e) {
            if (entityManager != null) {
                entityManager.getTransaction().rollback();
            }
            throw new GestorException(e.getMessage());
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    /**
     * CORRECTO
     * Esta función va a vender el coche a un propietario en caso de que el coche no esté
     * vendido y exista, creando así una venta
     *
     * @param dni             el dni del propietario
     * @param nombre          el nombre del propietario
     * @param matriculaCoche  la matrícula del coche a vender
     * @param idConcesionario el id del concesionario en el que se va a realizar la venta
     * @param precioPactado   el precio pactado de la venta
     * @throws GestorException en caso de que el coche, el concesionario o algún campo sea incorrecto
     *                         o en caso de un error con EntityManager
     */
    public void venderCoche(String dni, String nombre, String matriculaCoche, long idConcesionario, double precioPactado)
            throws GestorException {
        // Comprobamos si el formato del dni y el nombre son válidos
        if (!dni.toUpperCase().matches("^[0-9]{8}([A-Z]|[a-z])$") || nombre.isEmpty()) {
            throw new GestorException("El nombre y/o el dni no son válidos");
        }
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            // Comenzamos la transacción
            entityManager.getTransaction().begin();
            // Vamos a obtener el concesionario por su id en caso de que exista
            Concesionario concesionario = obtenerConcesionario(idConcesionario, entityManager);
            // En caso de que no existe el concesionario lanzamos excepción
            if (concesionario == null) {
                throw new GestorException("El concesionario no existe");
            }
            // Obtenemos el coche
            model.Coche coche = obtenerCoche(matriculaCoche.toUpperCase(), entityManager);
            if (coche == null) {
                throw new GestorException("El coche no existe");
            }
            // Vamos a comprobar si el coche ya ha sido vendido
            if (coche.getPropietario() != null) {
                throw new GestorException("El coche ya ha sido vendido antes");
            }
            // Comprobamos que el coche esté en el mismo concesionario en el cual se quiere vender
            if (coche.getConcesionario().getId() != idConcesionario) {
                throw new GestorException("El coche a vender no pertenece al concesionario con id " + idConcesionario);
            }
            // Vamos a intentar obtener el propietario en caso de que exista ya en la base de datos porque ya haya comprado antes
            /* NOTA IMPORTANTE UTILIZO JPQL PORQUE ENTONCES NO PODRÍA OBTENER EL PROPIETARIO, YA QUE DEPENDE DE SU ID,
             * NO DE SU DNI COMO CLAVE PRIMARIA PARA SER OBTENIDO POR EL find() */
            TypedQuery<Propietario> propietarioTypedQuery = entityManager
                    .createQuery("select p from Propietario p where p.dni = :dni", Propietario.class)
                    .setParameter("dni", dni.toUpperCase());
            List<Propietario> propietarios = propietarioTypedQuery.getResultList();
            Propietario propietario;
            // En caso de que no haya propietario, se crea
            if (validarExistencia(propietarios)) {
                propietario = new Propietario(dni.toUpperCase(), nombre); // TRANSIENT
                // Persistimos el propietario
                entityManager.persist(propietario); // MANAGED
            } else {
                propietario = propietarios.getFirst(); // MANAGED
            }
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
            // Borramos la relación entre el coche y el concesionario
            concesionario.removeCoche(coche);
            // Terminamos la transacción
            entityManager.getTransaction().commit();
        } catch (GestorException | PersistenceException e) {
            if (entityManager != null) {
                entityManager.getTransaction().rollback();
            }
            throw new GestorException(e.getMessage());
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    // ------------------JPQL------------------

    /**
     * CORRECTO
     * Esta función va a escribir en un archivo txt
     * la información de todos los coches de un concesionario
     * concreto dependiendo del id pasado por parámetros que no
     * tengan dueño
     *
     * @param idConcesionario id del concesionario a buscar
     * @throws GestorException en caso de que no se encuentre el concesionario,
     *                         en caso de que haya un problema al utilizar el EntityManager o
     *                         en caso de que haya un problema al utilizar el BufferedWriter
     */
    public void stockConcesionario(long idConcesionario) throws GestorException {
        EntityManager entityManager = null;
        BufferedWriter bw = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            bw = new BufferedWriter(new FileWriter(prop.getProperty("stockConcesionarioTXT")));
            // Comenzamos la transacción
            entityManager.getTransaction().begin();
            // Creamos una consulta tipada para obtener la lista de coches del concesionario concreto sin propietario (Aunque esto ya se maneja)
            TypedQuery<Coche> concesionarioTypedQuery = entityManager
                    .createQuery("select c from Coche c where c.concesionario.id = :idConcesionario " +
                            "and c.propietario is null", Coche.class)
                    .setParameter("idConcesionario", idConcesionario);
            List<Coche> coches = concesionarioTypedQuery.getResultList();
            // En caso de que no exista el concesionario con el id especificado lanzamos excepción
            if (validarExistencia(coches)) {
                throw new GestorException("No hay coches en el concesionario");
            }
            // Escribimos en el resumen los coches del concesionario (que no tienen propietario)
            bw.write("Coches del concesionario con id " + idConcesionario + ":\n" + coches.stream()
                    // Convertimos el objeto a String
                    .map(Coche::toString)
                    // Los unimos en String que se vaya separando por un intro
                    .collect(Collectors.joining("\n")));
            // Cerramos la transacción
            entityManager.getTransaction().commit();
        } catch (IOException | GestorException | PersistenceException e) {
            if (entityManager != null) {
                entityManager.getTransaction().rollback();
            }
            throw new GestorException(e.getMessage());
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    /**
     * CORRECTO
     * Esta función va a crear un archivo txt que recoja todas las reparaciones
     * llevadas a cabo por un mecánico concreto
     *
     * @param idMecanico id del mecánico a buscar
     * @throws GestorException en caso de que no se encuentre al mecánico,
     *                         en caso de que haya algún problema al escribir o en caso de que haya algún error con el EntityManager
     */
    public void historialMecanico(long idMecanico) throws GestorException {
        EntityManager entityManager = null;
        BufferedWriter bw = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            bw = new BufferedWriter(new FileWriter(prop.getProperty("historialMecanicoTXT")));

            // Comenzamos la transacción
            entityManager.getTransaction().begin();
            // Creamos una TypedQuery para obtener la lista de reparaciones con el id del mecánico
            TypedQuery<Reparacion> mecanicoTypedQuery = entityManager.createQuery("select r from Reparacion r" +
                            " where r.mecanico.id = :idMecanico",
                    Reparacion.class).setParameter("idMecanico", idMecanico);
            // Guardamos la lista devuleta por la TypedQuery
            List<Reparacion> reparaciones = mecanicoTypedQuery.getResultList();
            // En caso de que no haya reparaciones
            if (validarExistencia(reparaciones)) {
                throw new GestorException("No existen reparaciones para el mecánico");
            }
            bw.write("Historial de reparaciones:\n"
                    + reparaciones.stream().map(r -> "Reparación -> Fecha: %s | Matrícula coche: %s"
                            .formatted(r.getFecha().toString(), r.getCoche().getMatricula()))
                    .collect(Collectors.joining("\n")));
            // Terminamos la transacción
            entityManager.getTransaction().commit();
        } catch (IOException | GestorException | PersistenceException e) {
            if (entityManager != null) {
                entityManager.getTransaction().rollback();
            }
            throw new GestorException(e.getMessage());
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    /**
     * CORRECTO
     * Esta función va a escribir en un informe text las ventas que ha tenido
     * un determinado concesionario (dependiendo del id pasado) y el importe
     * total de todas sus ventas
     *
     * @param idConcesionario el id del concesionario a buscar
     * @throws GestorException en caso de que no se encuentre el concesionario,
     *                         en caso de que haya problemas con el EntityManager o
     *                         en caso de que haya algún problema al escribir los datos en el txt
     *
     */
    public void ventasPorConcesionario(long idConcesionario) throws GestorException {
        EntityManager entityManager = null;
        BufferedWriter bw = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            bw = new BufferedWriter(new FileWriter(prop.getProperty("informeVentaConcesionarioTXT")));
            // Comenzamos la transacción
            entityManager.getTransaction().begin();
            // Obtenemos las ventas
            List<Venta> ventas = entityManager.createQuery("select v from Venta v " +
                            "where v.concesionario.id = :id", Venta.class)
                    .setParameter("id", idConcesionario).getResultList();
            // En caso de que no haya ventas
            if (validarExistencia(ventas)) {
                throw new GestorException("No hay ventas del concesionario");
            }
            // Calculamos el total de las ventas, si llegaos a este punto sin lanzar excepción debe de devolver un double
            double total = entityManager.createQuery("select sum(v.precioFinal) from Venta v", Double.class).getSingleResult();
            // Escribimos el resultado la consulta en el txt
            bw.write("Ventas del concesionario con id " + idConcesionario + ":\n"
                    + ventas.stream().map(Venta::toString).collect(Collectors.joining("\n")) +
                    // Llamamos a la función que va a devolver el precio total de todas las ventas
                    "\n\nImporte total concesionario: " + total + "€");
            // Cerramos la transacción
            entityManager.getTransaction().commit();
        } catch (IOException | GestorException | PersistenceException e) {
            if (entityManager != null) {
                entityManager.getTransaction().rollback();
            }
            throw new GestorException(e.getMessage());
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    /**
     * Esta función va a escribir en un informe txt
     * el coste total actual de un coche concreto
     *
     * @param matriculaCoche la matrícula del coche a calcular su coste total actual
     * @throws GestorException en caso de cualquier problema
     */
    public void costeActualCoche(String matriculaCoche) throws GestorException {
        EntityManager entityManager = null;
        BufferedWriter bw = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            bw = new BufferedWriter(new FileWriter(prop.getProperty("costeActualCocheTXT")));
            // Comenzamos la transacción
            entityManager.getTransaction().begin();
            //List<Double> total = entityManager.createQuery("select ")
            //bw.write("Coste total actual del coche: " + calcularCosteActualCoche(coche) + "€");
            // Cerramos la transacción
            entityManager.getTransaction().commit();

        } catch (IOException | PersistenceException /*| GestorException*/ e) {
            if (entityManager != null) {
                entityManager.getTransaction().rollback();
            }
            throw new GestorException(e.getMessage());
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    /* IMPORTANTE: A PARTIR DE ESTE PUNTO LAS FUNCIONES SON LAS NECESARIAS
     * PARA POSTERIORMENTE MOSTRAR EN EL MENÚ, SON FUNCIONES QUE DEVUELVEN
     * LISTAS CON INFORMACIÓN O DIRECTAMENTE EL OBJETO EN CASO DE LOS MECÁNICOS,
     * HE DECIDIDO HACERLO DE ESTA FORMA PORQUE AL INTENTAR DEVOLVER LISTAS CON
     * OBJETOS COMPLETOS QUE A SU VEZ TIENE OBJETOS COMPLETOS DENTRO, COMO TIENEN
     * LA CARGA LAZY, PEREZOSA SE QUEDAN OBJETOS FANTASMA (PROXY) Y DA ERROR,
     * POR LO QUE SIMPLEMENTE DEVUELVO LA INFORMACIÓN QUE CREO QUE ES RELEVANTE
     * PARA EL USUARIO */

    /**
     * Esta función va a devolver la lista de concesionarios
     * que hay en la base de datos
     *
     * @return la lista con la información de los concesionarios que hay
     * @throws GestorException en caso de que no haya concesionarios
     *                         o algún problema con el EntityManager
     */
    public List<String> mostrarConcesionarios() throws GestorException {
        // Creamos la lista de concesionarios
        List<String> concesionariosInfo;
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            // Comenzamos la transacción
            entityManager.getTransaction().begin();
            // Creamos una consulta tipada que va a devolver todos los concesionarios
            List<Concesionario> concesionarios = entityManager
                    .createQuery("select c from Concesionario c", Concesionario.class).getResultList();
            if (validarExistencia(concesionarios)) {
                throw new GestorException("No hay concesionarios registrados");
            }
            concesionariosInfo = concesionarios.stream().map(c -> c.getId() + " | " + c.getNombre() + " | "
                    + c.getDireccion()).toList();
            // Terminamos la transacción
            entityManager.getTransaction().commit();
        } catch (GestorException | PersistenceException e) {
            if (entityManager != null) {
                entityManager.getTransaction().rollback();
            }
            throw new GestorException(e.getMessage());
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return concesionariosInfo;
    }

    /**
     * Esta función va a devolver una lista de coches de un concesionario
     * concreto o todos los coches de la bd
     *
     * @param idConcesionario el id del concesionario a buscar (menor o igual a 0 si no queremos por concesionario)
     * @return la lista de matrículas de los coches
     * @throws GestorException en caso de cualquier error
     */
    public List<String> mostrarCoches(long idConcesionario) throws GestorException {
        // Almacenamos los coches que vamos a obtener
        List<String> cochesMatriculas;
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            // Comenzamos la transacción
            entityManager.getTransaction().begin();
            // Obtenemos todos los coches
            java.util.List<Coche> coches = entityManager.createQuery("select c from Coche c", Coche.class).getResultList();
            // En caso de que no se hayan obtenidos coches
            if (validarExistencia(coches)) {
                throw new GestorException("No hay coches registrados");
            }
            cochesMatriculas = coches.stream().map(Coche::getMatricula).toList();
            // En caso de que el id sea mayor a 0 buscamos por id del concesionario
            if (idConcesionario > 0) {
                cochesMatriculas = coches.stream().filter(c -> c.getConcesionario() != null)
                        .filter(c -> c.getConcesionario().getId() == idConcesionario)
                        .map(Coche::getMatricula).toList();
            }
            // En caso de que no haya concesionarios con el id específico después de filtrar
            if (validarExistencia(cochesMatriculas)) {
                throw new GestorException("No existen coches que pertenezcan al concesionario con id " + idConcesionario);
            }
            // Terminamos la transacción
            entityManager.getTransaction().commit();
        } catch (GestorException | PersistenceException e) {
            if (entityManager != null) {
                entityManager.getTransaction().rollback();
            }
            throw new GestorException(e.getMessage());
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return cochesMatriculas;
    }

    /**
     * Esta función va a devolver una lista de todos los equipamientos de la bd
     * en caso de que haya
     *
     * @return la lista con la información de los equipamientos de la bd
     * @throws GestorException en caso de cualquier error
     */
    public List<String> mostrarEquipamientos() throws GestorException {
        List<String> equipamientosInfo;
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            // Comenzamos la transacción
            entityManager.getTransaction().begin();
            // Guardamos los equipamientos registrados en la base de datos
            java.util.List<Equipamiento> equipamientos = entityManager
                    .createQuery("select e from Equipamiento e", Equipamiento.class).getResultList();
            if (validarExistencia(equipamientos)) {
                throw new GestorException("No hay equipamientos registrados");
            }
            equipamientosInfo = equipamientos.stream().map(e -> e.getId() + " | " + e.getNombre()).toList();
            // Terminamos la transacción
            entityManager.getTransaction().commit();
        } catch (GestorException | PersistenceException e) {
            if (entityManager != null) {
                entityManager.getTransaction().rollback();
            }
            throw new GestorException(e.getMessage());
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return equipamientosInfo;
    }

    /**
     * Esta función va a devolver la lista de todos los mecánicos
     * de la bd en caso de que haya
     *
     * @return la lista de mecánicos
     * @throws GestorException en caso de cualquier error
     */
    public List<Mecanico> mostrarMecanicos() throws GestorException {
        List<Mecanico> mecanicos;
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            // Comenzamos la transacción
            entityManager.getTransaction().begin();
            // Creamos una consulta tipada la cual va a obetener todos los mecánicos de la bd
            mecanicos = entityManager.createQuery("select  m from Mecanico m", Mecanico.class).getResultList();
            if (validarExistencia(mecanicos)) {
                throw new GestorException("No hay mecanicos registrados");
            }
            // Cerramos la transacción
            entityManager.getTransaction().commit();
        } catch (GestorException | PersistenceException e) {
            if (entityManager != null) {
                entityManager.getTransaction().rollback();
            }
            throw new GestorException(e.getMessage());
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return mecanicos;
    }

    // --------------------MÉTODOS HELPER--------------------

    /**
     * Esta función va a devolver una lista el coche
     * devuelto por find(), este busca un coche
     * por su matrícula
     *
     * @param matriculaCoche la matrícula del coche a obtener
     * @param entityManager  el EntityManager de la transacción
     * @return el coche en caso de que exita, si no null
     * @throws PersistenceException en caso de que haya algún error
     */
    private Coche obtenerCoche(String matriculaCoche, EntityManager entityManager) throws PersistenceException {
        // Vamos a obtener el coche por su matrícula en caso de que exista
        return entityManager.find(Coche.class, matriculaCoche);
    }

    /**
     * Esta función va a devolver un concesionario por su id en
     * caso de que exista, en caso de que no null
     *
     * @param idConcesionario el id del concesionario
     * @param entityManager   el EntityManager
     * @return el concesionario
     */
    private Concesionario obtenerConcesionario(long idConcesionario, EntityManager entityManager) {
        return entityManager.find(Concesionario.class, idConcesionario);
    }

    /**
     * Esta función va devolver el Equipamiento
     * pasando un id o null en caso de que no exista
     *
     * @param idEquipamiento el id del equipamiento
     * @param entityManager  el EntityManager
     * @return el Equipamiento o null
     */
    private Equipamiento obtenerEquipamiento(long idEquipamiento, EntityManager entityManager) {
        return entityManager.find(Equipamiento.class, idEquipamiento);
    }

    /**
     * Esta función va a devolver el mecánico con el id pasado por parámetros,
     * en caso de que no exista se devuelve null
     *
     * @param idMecanico    el id del mecánico
     * @param entityManager el EntityManager
     * @return el mecánico o null
     */
    private Mecanico obtenerMecanico(long idMecanico, EntityManager entityManager) {
        return entityManager.find(Mecanico.class, idMecanico);
    }

    /**
     * CORRECTO
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
}