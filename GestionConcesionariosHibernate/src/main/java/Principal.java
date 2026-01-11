import exception.GestorException;
import model.Mecanico;
import service.GestorService;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Principal {

    private static final Scanner sc = new Scanner(System.in);
    private static GestorService gestor;

    public static void main(String[] args) {
        System.out.println("INICIANDO SISTEMA DE GESTIÓN DE CONCESIONARIOS (JPA/HIBERNATE)");

        try {
            // Creamos el gestor
            gestor = new GestorService();
            // En caso de que se haya creado correctamente mostramos el mensaje de conexión establecida
            System.out.println("[INFO] Conexión establecida y EntityManagerFactory cargado.");
        } catch (Exception e) {
            System.err.println("ERROR FATAL: No se pudo conectar a la base de datos.");
            System.err.println("Detalles: " + e.getMessage());
            return;
        }

        int opcion = 0;
        // Mientras la opción sea distinta de 0 seguimos mostrando el menú
        do {
            // Mostramos el menú
            mostrarMenu();
            // Leemos la opción elegida por el usuario
            opcion = leerEntero("Seleccione una opción: ");
            // El switch con las diferentes opciones
            procesarOpcion(opcion);
        } while (opcion != 0);

        System.out.println("Fin del programa. ¡Hasta luego!");
    }

    /**
     * Esta función va a mostrar en consola las diferentes
     * opciones del menú
     */
    private static void mostrarMenu() {
        System.out.println("\n===========================================");
        System.out.println("           MENÚ PRINCIPAL");
        System.out.println("===========================================");
        System.out.println("1.  Cargar Datos de Prueba (Borra todo lo anterior)");
        System.out.println("2.  Alta de Concesionario");
        System.out.println("3.  Alta de Coche");
        System.out.println("4.  Instalar Extra a un Coche");
        System.out.println("5.  Registrar Reparación");
        System.out.println("6.  Vender Coche");
        System.out.println("7.  Listar Stock de Concesionario (TXT generado)");
        System.out.println("8.  Historial de Mecánico (TXT generado)");
        System.out.println("9.  Ventas por Concesionario (TXT generado)");
        System.out.println("10. Calcular Coste Actual de Coche (TXT generado)");
        System.out.println("0.  Salir");
        System.out.println("===========================================");
    }

    /**
     * Esta función va a recibir la opción
     * por parámetros y va a llamar a la función
     * correspondiente según este
     *
     * @param opcion la opción pasada
     */
    private static void procesarOpcion(int opcion) {
        try {
            switch (opcion) {
                case 1 -> cargarDatosPrueba();
                case 2 -> altaConcesionario();
                case 3 -> altaCoche();
                case 4 -> instalarExtra();
                case 5 -> registrarReparacion();
                case 6 -> venderCoche();
                case 7 -> listarStockConcesionario();
                case 8 -> historialMecanico();
                case 9 -> ventasPorConcesionario();
                case 10 -> costeActualCoche();
                case 0 -> System.out.println("Cerrando recursos...");
                default -> System.out.println("Opción no válida.");
            }
        } catch (GestorException e) {
            System.out.println("ERROR DE GESTIÓN: " + e.getMessage());
        } catch (DateTimeParseException e) {
            System.out.println("ERROR DE FECHA: Formato incorrecto. Use dd/MM/yyyy.");
        } catch (NumberFormatException e) {
            System.out.println("ERROR DE FORMATO: Has introducido texto donde iba un número.");
        } catch (Exception e) {
            System.out.println("ERROR INESPERADO: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- MÉTODOS DE LA OPCIÓN 1 ---

    /**
     * Esta función va a llamar a la función cargarDatosPrueba()
     * mostrando mensajes para que el usuario tenga noción
     * de lo que está pasando
     *
     * @throws GestorException
     */
    private static void cargarDatosPrueba() throws GestorException {
        System.out.println("Borrando base de datos y cargando datos semilla...");
        // Se llama a la función
        gestor.cargarDatosPrueba();
        System.out.println("Datos de prueba cargados correctamente.");
    }

    // --- MÉTODOS DE LA OPCIÓN 2 ---

    /**
     * Esta función va a crear un concesionario,
     * se le solicitará los datos al usuario y se
     * llamará a la función darAltaConcesionario()
     *
     * @throws GestorException en caso de cualquier error
     */
    private static void altaConcesionario() throws GestorException {
        System.out.println("\n--- Nuevo Concesionario ---");
        // Solicitamos el nombre del concesionario
        String nombre = leerTexto("Nombre del concesionario: ");
        // Solicitamos la dirección
        String direccion = leerTexto("Dirección: ");
        // Llamamos a la función para crearlo
        gestor.darAltaConcesionario(nombre, direccion);
        System.out.println("Concesionario creado con éxito.");
    }

    /**
     * Esta función va a solicitar los datos de un coche
     * para después darlo de alta con la función darAltaCoche()
     *
     * @throws GestorException       en caso de cualquier error
     * @throws NumberFormatException en caso de error de casteo
     */
    private static void altaCoche() throws GestorException, NumberFormatException {
        System.out.println("\n--- Nuevo Coche ---");
        // Listamos todos los concesionarios
        listarConcesionariosPantalla();
        // Elegimos el id del concesionario
        long idConcesionario = leerLong("ID del Concesionario donde ubicarlo: ");
        // Solicitamos la matrícula del nuevo coche
        String matricula = leerTexto("Matrícula (4 números + 3 letras): ");
        // Solicitamos la marca
        String marca = leerTexto("Marca: ");
        // Solicitamos el modelo
        String modelo = leerTexto("Modelo: ");
        // Solicitamos el precio base
        double precio = leerDouble("Precio Base: ");
        // Llamamos a a la función para crearlo
        gestor.darAltaCoche(matricula, marca, modelo, precio, idConcesionario);
        System.out.println("Coche registrado correctamente en el stock.");
    }

    // --- MÉTODOS DE LA OPCIÓN 3 ---

    /**
     * Esta función va a mostrar los coches y extras que hay
     * para instalar un extra existente a un coche
     *
     * @throws GestorException       en caso de cualquier error
     * @throws NumberFormatException en caso de error de casteo
     */
    private static void instalarExtra() throws GestorException, NumberFormatException {
        System.out.println("\n--- Instalación de Extras ---");
        // Mostramos los coches disponibles
        listarCochesPantalla(-1);
        // Solicitamos la matrícula elegida
        String matricula = leerTexto("Introduzca la Matrícula del coche: ");
        // Mostramos los equipamientos disponibles
        listarEquipamientosPantalla();
        // Solicitamos el equipamiento disponible
        long idEquipamiento = leerLong("ID del Equipamiento a instalar: ");
        // Llamamos a la función para instalar el extra
        double nuevoPrecio = gestor.instalarExtra(matricula, idEquipamiento);
        System.out.printf("Extra instalado. Nuevo valor total del coche: %.2f €%n", nuevoPrecio);
    }

    /**
     * Esta función va a mostrar los coches y mecánicos
     * disponibles en la bd para registrar una reparación de
     * un coche concreto
     *
     * @throws GestorException        en caso de casi cualquier error
     * @throws DateTimeParseException en caso de que al parsear la fecha de error
     * @throws NumberFormatException  en caso de error de casteo
     */
    private static void registrarReparacion() throws GestorException, DateTimeParseException, NumberFormatException {
        System.out.println("\n--- Nueva Reparación ---");
        // Mostramos los coches
        listarCochesPantalla(-1);
        // Solicitamos la matrícula
        String matricula = leerTexto("Matrícula del coche: ");
        // Mostramos los mecánicos
        listarMecanicosPantalla();
        // Solicitamos el id del mecánico
        long idMecanico = leerLong("ID del Mecánico: ");
        // Solicitamos la fecha
        String fecha = leerTexto("Fecha (dd/MM/yyyy): ");
        // Solicitamos el coste de la reparación
        double coste = leerDouble("Coste de la reparación: ");
        // Solicitamos la descripción
        String descripcion = leerTexto("Descripción breve: ");
        // Llamamos a la función que va a registrar la reparación
        gestor.registrarReparacion(matricula, idMecanico, fecha, coste, descripcion);
        System.out.println("Reparación registrada correctamente.");
    }

    // --- MÉTODOS DE LA OPCIÓN 4 ---

    /**
     * Esta función va a solicitar el id del concesionario a elegir,
     * la matrícula del coche a vender, generando así una venta
     *
     * @throws GestorException       en caso de cualquier error
     * @throws NumberFormatException en caso de error de casteo
     */
    private static void venderCoche() throws GestorException, NumberFormatException {
        System.out.println("\n--- Venta de Vehículo ---");
        // Elegimos el concesionario vendedor, mostrándo todos
        listarConcesionariosPantalla();
        // Solicitamos el id del concesionario
        long idConcesionario = leerLong("ID del Concesionario que vende): ");
        // Mostramos los coches del concesionario correspondiente en caso de que exista
        listarCochesPantalla(idConcesionario);
        String matricula = leerTexto("Matrícula del coche a vender: ");

        String dni = leerTexto("DNI del comprador (8 números + Letra): ");
        String nombre = leerTexto("Nombre del comprador: ");
        double precioFinal = leerDouble("Precio final pactado: ");

        gestor.venderCoche(dni, nombre, matricula, idConcesionario, precioFinal);
        System.out.println("¡Venta realizada! El coche ahora tiene propietario y se ha generado el histórico.");
    }

    // --- MÉTODOS DE LA OPCIÓN 5 (INFORMES) ---

    /**
     * Esta función va a solicitar un concesionario y generar
     * un informe sobre el stock de coches que tiene
     *
     * @throws GestorException       en caso de cualquier error
     * @throws NumberFormatException en caso de error de casteo
     */
    private static void listarStockConcesionario() throws GestorException, NumberFormatException {
        listarConcesionariosPantalla(); // Ayuda visual
        long id = leerLong("Introduce el ID del concesionario para generar el informe: ");
        gestor.stockConcesionario(id);
        System.out.println("Informe generado en el archivo de texto correspondiente.");
    }

    /**
     * Esta función va a solicitar el id de un mecánico
     * y va a generar un txt con su historial de reparaciones
     *
     * @throws GestorException       en caso de cualquier error
     * @throws NumberFormatException en caso de error de casteo
     */
    private static void historialMecanico() throws GestorException, NumberFormatException {
        listarMecanicosPantalla(); // Ayuda visual
        long id = leerLong("Introduce el ID del mecánico: ");
        gestor.historialMecanico(id);
        System.out.println("Informe generado en el archivo de texto correspondiente.");
    }

    /**
     * Esta función va a mostrar en un txt todas las ventas de un concesionario
     * especificado, solicitando el id del concesionario
     *
     * @throws GestorException       en caso de cualquier error
     * @throws NumberFormatException en caso de error de casteo
     */
    private static void ventasPorConcesionario() throws GestorException, NumberFormatException {
        listarConcesionariosPantalla(); // Ayuda visual
        long id = leerLong("Introduce el ID del concesionario: ");
        gestor.ventasPorConcesionario(id);
        System.out.println("Informe generado en el archivo de texto correspondiente.");
    }

    /**
     * Esta función va a solicitar la matrícula de un coche
     * y va a calcular y mostrar en un txt el coste actual del coche
     *
     * @throws GestorException
     */
    private static void costeActualCoche() throws GestorException {
        listarCochesPantalla(-1); // Ayuda visual
        String matricula = leerTexto("Introduce la matrícula: ");
        gestor.costeActualCoche(matricula);
        System.out.println("Informe generado en el archivo de texto correspondiente.");
    }

    // --- MÉTODOS DE AYUDA VISUAL ---

    /**
     * Esta función va a imprimis la lista de todos los
     * concesionario que hay en la base de datos
     * @throws GestorException en caso de cualquier error
     */
    private static void listarConcesionariosPantalla() throws GestorException {
        System.out.println("Concesionarios Disponibles:");
        List<String> lista = gestor.mostrarConcesionarios();
        lista.forEach(System.out::println);
        System.out.println("---------------------------------");
    }

    /**
     * Esta función va a imprimir la lista de todos los mecánicos
     * disponibles en la base de datos
     *
     * @throws GestorException en caso de cualquier error
     */
    private static void listarMecanicosPantalla() throws GestorException {
        System.out.println("Mecánicos Disponibles:");
        List<Mecanico> lista = gestor.mostrarMecanicos();
        lista.forEach(System.out::println);
        System.out.println("---------------------------------");
    }

    /**
     * Esta función va a imprimir la lista de todos los
     * equipamientos disponibles en la base de datos
     *
     * @throws GestorException en caso de cualquier error
     */
    private static void listarEquipamientosPantalla() throws GestorException {
        System.out.println("Equipamientos Disponibles:");
        List<String> lista = gestor.mostrarEquipamientos();
        lista.forEach(System.out::println);
        System.out.println("---------------------------------");
    }

    /**
     * Esta función va a mostrar los coches de
     * un concesionario concreto o de toda la base de datos
     *
     * @throws GestorException en caso de cualquier error
     */
    private static void listarCochesPantalla(long idConcesionario) throws GestorException {
        System.out.println("Todos los Coches Disponibles:");
        List<String> lista = gestor.mostrarCoches(idConcesionario);
        lista.forEach(System.out::println);
        System.out.println("---------------------------------");
    }

    // --- UTILIDADES DE LECTURA ---

    /**
     * Esta función va a leer un String
     * y va a quitar los espacios de alrededor
     *
     * @param mensaje el String
     * @return devuelve el String sin los espacios alrededor
     */
    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return sc.nextLine().trim();
    }

    /**
     * Esta función va a recibir un mensaje,
     * en este caso un entero, en caso de que
     * no sea un entero, atrapará la excepción
     * al intentar parsear, mostrará el mensaje de error
     * y devolverá un -1 para que cargue otra vez el menú
     *
     * @param mensaje el mensaje que se espera que sea un entero
     * @return la opción o -1 en caso de error
     */
    private static int leerEntero(String mensaje) {
        System.out.print(mensaje);
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Debes introducir un número entero.");
            return -1;
        }
    }

    /**
     * Esta función va a intentar leer un long, para
     * los id, en caso de que no sea un long se lanzará
     * excepción
     *
     * @param mensaje el long a castear
     * @return el long casteado
     * @throws NumberFormatException lanza excepción en caso de que no se pueda castear
     */
    private static long leerLong(String mensaje) throws NumberFormatException {
        System.out.print(mensaje);
        return Long.parseLong(sc.nextLine().trim());
    }

    /**
     * Esta función va a intentar castear a double
     * el String pasado por parámetros, en caso
     * de error lanzará excepción, se utilizará
     * para la lectura de precios
     *
     * @param mensaje el double a castear
     * @return el double casteado
     * @throws NumberFormatException en caso de que no se pueda castear a double
     */
    private static double leerDouble(String mensaje) throws NumberFormatException {
        System.out.print(mensaje);
        return Double.parseDouble(sc.nextLine().trim());
    }
}