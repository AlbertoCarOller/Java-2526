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
        System.out.println("🚗 INICIANDO SISTEMA DE GESTIÓN DE CONCESIONARIOS (JPA/HIBERNATE) 🚗");

        try {
            gestor = new GestorService();
            System.out.println("[INFO] Conexión establecida y EntityManagerFactory cargado.");
        } catch (Exception e) {
            System.err.println("🔴 ERROR FATAL: No se pudo conectar a la base de datos.");
            System.err.println("Detalles: " + e.getMessage());
            return;
        }

        int opcion = 0;
        do {
            mostrarMenu();
            opcion = leerEntero("Seleccione una opción: ");
            procesarOpcion(opcion);
        } while (opcion != 0);

        System.out.println("👋 Fin del programa. ¡Hasta luego!");
    }

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
                default -> System.out.println("⚠️ Opción no válida.");
            }
        } catch (GestorException e) {
            System.out.println("❌ ERROR DE GESTIÓN: " + e.getMessage());
        } catch (DateTimeParseException e) {
            System.out.println("❌ ERROR DE FECHA: Formato incorrecto. Use dd/MM/yyyy.");
        } catch (NumberFormatException e) {
            System.out.println("❌ ERROR DE FORMATO: Has introducido texto donde iba un número.");
        } catch (Exception e) {
            System.out.println("❌ ERROR INESPERADO: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- MÉTODOS DE LA OPCIÓN 1 ---
    private static void cargarDatosPrueba() throws GestorException {
        System.out.println("⏳ Borrando base de datos y cargando datos semilla...");
        gestor.cargarDatosPrueba();
        System.out.println("✅ Datos de prueba cargados correctamente.");
    }

    // --- MÉTODOS DE LA OPCIÓN 2 ---
    private static void altaConcesionario() throws GestorException {
        System.out.println("\n--- Nuevo Concesionario ---");
        String nombre = leerTexto("Nombre del concesionario: ");
        String direccion = leerTexto("Dirección: ");
        gestor.darAltaConcesionario(nombre, direccion);
        System.out.println("✅ Concesionario creado con éxito.");
    }

    private static void altaCoche() throws GestorException {
        System.out.println("\n--- Nuevo Coche ---");
        listarConcesionariosPantalla();

        long idConcesionario = leerLong("ID del Concesionario donde ubicarlo: ");
        String matricula = leerTexto("Matrícula (4 números + 3 letras): ");
        String marca = leerTexto("Marca: ");
        String modelo = leerTexto("Modelo: ");
        double precio = leerDouble("Precio Base: ");

        gestor.darAltaCoche(matricula, marca, modelo, precio, idConcesionario);
        System.out.println("✅ Coche registrado correctamente en el stock.");
    }

    // --- MÉTODOS DE LA OPCIÓN 3 ---
    private static void instalarExtra() throws GestorException {
        System.out.println("\n--- Instalación de Extras ---");
        // 1. Mostramos coches disponibles
        listarCochesGlobalPantalla();
        String matricula = leerTexto("Introduzca la Matrícula del coche: ");

        // 2. Mostramos equipamientos disponibles
        listarEquipamientosPantalla();
        long idEquipamiento = leerLong("ID del Equipamiento a instalar: ");

        double nuevoPrecio = gestor.instalarExtra(matricula, idEquipamiento);
        System.out.printf("✅ Extra instalado. Nuevo valor total del coche: %.2f €%n", nuevoPrecio);
    }

    private static void registrarReparacion() throws GestorException {
        System.out.println("\n--- Nueva Reparación ---");
        // 1. Mostramos coches
        listarCochesGlobalPantalla();
        String matricula = leerTexto("Matrícula del coche: ");

        // 2. Mostramos mecánicos
        listarMecanicosPantalla();
        long idMecanico = leerLong("ID del Mecánico: ");

        String fecha = leerTexto("Fecha (dd/MM/yyyy): ");
        double coste = leerDouble("Coste de la reparación: ");
        String descripcion = leerTexto("Descripción breve: ");

        gestor.registrarReparacion(matricula, idMecanico, fecha, coste, descripcion);
        System.out.println("✅ Reparación registrada correctamente.");
    }

    // --- MÉTODOS DE LA OPCIÓN 4 ---
    private static void venderCoche() throws GestorException {
        System.out.println("\n--- Venta de Vehículo ---");
        // 1. Elegimos concesionario vendedor
        listarConcesionariosPantalla();
        long idConcesionario = leerLong("ID del Concesionario que vende: ");

        // 2. Elegimos coche
        listarCochesGlobalPantalla();
        String matricula = leerTexto("Matrícula del coche a vender: ");

        String dni = leerTexto("DNI del comprador (8 números + Letra): ");
        String nombre = leerTexto("Nombre del comprador: ");
        double precioFinal = leerDouble("Precio final pactado: ");

        gestor.venderCoche(dni, nombre, matricula, idConcesionario, precioFinal);
        System.out.println("✅ ¡Venta realizada! El coche ahora tiene propietario y se ha generado el histórico.");
    }

    // --- MÉTODOS DE LA OPCIÓN 5 (INFORMES) ---
    private static void listarStockConcesionario() throws GestorException {
        listarConcesionariosPantalla(); // Ayuda visual
        long id = leerLong("Introduce el ID del concesionario para generar el informe: ");
        gestor.stockConcesionario(id);
        System.out.println("📄 Informe generado en el archivo de texto correspondiente.");
    }

    private static void historialMecanico() throws GestorException {
        listarMecanicosPantalla(); // Ayuda visual
        long id = leerLong("Introduce el ID del mecánico: ");
        gestor.historialMecanico(id);
        System.out.println("📄 Informe generado en el archivo de texto correspondiente.");
    }

    private static void ventasPorConcesionario() throws GestorException {
        listarConcesionariosPantalla(); // Ayuda visual
        long id = leerLong("Introduce el ID del concesionario: ");
        gestor.ventasPorConcesionario(id);
        System.out.println("📄 Informe generado en el archivo de texto correspondiente.");
    }

    private static void costeActualCoche() throws GestorException {
        listarCochesGlobalPantalla(); // Ayuda visual
        String matricula = leerTexto("Introduce la matrícula: ");
        gestor.costeActualCoche(matricula);
        System.out.println("📄 Informe generado en el archivo de texto correspondiente.");
    }

    // --- MÉTODOS DE AYUDA VISUAL ---

    private static void listarConcesionariosPantalla() {
        try {
            System.out.println("🔽 Concesionarios Disponibles:");
            List<String> lista = gestor.mostrarConcesionarios();
            lista.forEach(System.out::println);
            System.out.println("---------------------------------");
        } catch (GestorException e) {
            System.out.println("ℹ️ No hay concesionarios para mostrar.");
        }
    }

    private static void listarMecanicosPantalla() {
        try {
            System.out.println("🔽 Mecánicos Disponibles:");
            List<Mecanico> lista = gestor.mostrarMecanicos();
            lista.forEach(System.out::println);
            System.out.println("---------------------------------");
        } catch (GestorException e) {
            System.out.println("ℹ️ No hay mecánicos para mostrar.");
        }
    }

    private static void listarEquipamientosPantalla() {
        try {
            System.out.println("🔽 Equipamientos Disponibles:");
            List<String> lista = gestor.mostrarEquipamientos();
            lista.forEach(System.out::println);
            System.out.println("---------------------------------");
        } catch (GestorException e) {
            System.out.println("ℹ️ No hay equipamientos para mostrar.");
        }
    }

    private static void listarCochesGlobalPantalla() {
        try {
            System.out.println("🔽 Todos los Coches Disponibles:");
            List<String> lista = gestor.mostrarCoches(-1);
            lista.forEach(System.out::println);
            System.out.println("---------------------------------");
        } catch (GestorException e) {
            System.out.println("ℹ️ No hay coches para mostrar.");
        }
    }

    // --- UTILIDADES DE LECTURA ---

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return sc.nextLine().trim();
    }

    private static int leerEntero(String mensaje) {
        System.out.print(mensaje);
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("⚠️ Debes introducir un número entero.");
            return -1;
        }
    }

    private static long leerLong(String mensaje) {
        System.out.print(mensaje);
        return Long.parseLong(sc.nextLine().trim());
    }

    private static double leerDouble(String mensaje) {
        System.out.print(mensaje);
        return Double.parseDouble(sc.nextLine().trim());
    }
}