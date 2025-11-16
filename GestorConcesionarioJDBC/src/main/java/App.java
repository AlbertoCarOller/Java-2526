import PersonalExceptions.ConcesionarioExcepcion;
import model.Coche;
import service.ConcensionarioService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {

    // Declaramos el Scanner y el Service como estáticos
    private static Scanner sc = new Scanner(System.in);
    private static ConcensionarioService gestor;

    public static void main(String[] args) {

        // --- 1. ARRANQUE DEL SERVICIO ---
        try {
            gestor = new ConcensionarioService();
        } catch (IOException e) {
            System.out.println("Error CRÍTICO: No se pudo cargar el archivo de configuración 'config.properties'.");
            System.out.println(e.getMessage());
            return; // Salir del main
        }

        // --- 2. GESTIÓN DE ESTADO DEL MENÚ ---
        Boolean esMySQL = null; // true = MySQL, false = SQLite, null = No elegido
        boolean salir = false;

        // --- 3. BUCLE PRINCIPAL DEL MENÚ ---
        while (!salir) {

            if (esMySQL == null) {
                // --- MENÚ DE SELECCIÓN DE BBDD ---
                esMySQL = mostrarMenuSeleccionDB();
                // Si el usuario elige '0' (Salir) desde el menú de selección, 'esMySQL' será null
                if(esMySQL == null) {
                    salir = true;
                }
            } else {
                // --- MENÚ DE OPERACIONES ---
                mostrarMenuOperaciones(esMySQL);
                int opcion;
                try {
                    opcion = Integer.parseInt(sc.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Error: Debes introducir un número.");
                    continue; // Vuelve al inicio del bucle
                }

                // --- INICIO DE LA CORRECCIÓN ---

                // Gestionamos las opciones de navegación (0 y 10) aquí
                if (opcion == 0) {
                    salir = true;
                    System.out.println("Saliendo del programa...");

                } else if (opcion == 10) {
                    esMySQL = null; // Pone el estado en null
                    System.out.println("Volviendo al menú de selección de BBDD...");
                    // No ponemos 'break' para que el 'if (!salir)' de abajo no se ejecute

                } else {
                    // Si no es 0 o 10, es una operación
                    ejecutarOpcion(opcion, esMySQL);
                    // Añadimos la pausa aquí, solo después de una operación
                    System.out.println("\n(Pulsa ENTER para continuar...)");
                    sc.nextLine();
                }

                // --- FIN DE LA CORRECCIÓN ---
            }
        } // Fin del while

        sc.close();
        System.out.println("Programa finalizado.");
    }

    /**
     * Muestra el menú para elegir la BBDD y la inicializa.
     * @return true si elige MySQL, false si elige SQLite, null si elige Salir.
     */
    private static Boolean mostrarMenuSeleccionDB() {
        System.out.println("\n--- BIENVENIDO AL GESTOR DEL CONCESIONARIO ---");
        System.out.println("Por favor, selecciona la base de datos para esta sesión:");
        System.out.println("1. Conectar e Inicializar MySQL");
        System.out.println("2. Conectar e Inicializar SQLite");
        System.out.println("---------------------------------------------");
        System.out.println("0. Salir");
        System.out.print("Elige una opción: ");

        try {
            int opcion = Integer.parseInt(sc.nextLine());
            switch (opcion) {
                case 1:
                    System.out.println("Inicializando MySQL (Creando tablas e importando CSV)...");
                    gestor.iniciarDataBase(true);
                    System.out.println("¡Éxito! Conectado a MySQL.");
                    return true; // true para MySQL
                case 2:
                    System.out.println("Inicializando SQLite (Creando tablas e importando CSV)...");
                    gestor.iniciarDataBase(false);
                    System.out.println("¡Éxito! Conectado a SQLite.");
                    return false; // false para SQLite
                case 0:
                    System.out.println("Saliendo...");
                    return null; // null para Salir
                default:
                    System.out.println("Opción no válida.");
                    return null; // Vuelve al menú de selección
            }
            // Capturamos las excepciones específicas de la inicialización
        } catch (NumberFormatException e) {
            System.out.println("Error: Introduce un número válido.");
            return null; // Vuelve al menú de selección
        } catch (FileNotFoundException e) {
            System.out.println("Error de Fichero: No se encontró un archivo (config.properties, .csv o .db)");
            System.out.println(e.getMessage());
            return null;
        } catch (IOException e) {
            System.out.println("Error de E/S: " + e.getMessage());
            return null;
        } catch (SQLException e) {
            System.out.println("Error de BBDD: No se pudo inicializar la base de datos.");
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Muestra el menú de operaciones principal.
     * @param esMySQL El estado de la BBDD actual (para mostrarlo en el título).
     */
    private static void mostrarMenuOperaciones(boolean esMySQL) {
        String tipoDB = esMySQL ? "MySQL" : "SQLite";
        System.out.println("\n--- GESTOR DE CONCESIONARIO --- [Usando: " + tipoDB + "]");
        System.out.println("1. Registrar Propietario Nuevo");
        System.out.println("2. Insertar Coche Nuevo (al concesionario)");
        System.out.println("3. Listar Coches (del Concesionario, sin dueño)");
        System.out.println("4. Listar Coches (con Propietario)");
        System.out.println("5. Modificar Coche (por matrícula)");
        System.out.println("6. Borrar Coche (por matrícula)");
        System.out.println("7. Realizar Traspaso");
        if (esMySQL) { // Opción solo para MySQL
            System.out.println("8. Buscar Coches por Marca (Proc. Almacenado)");
        }
        System.out.println("9. Generar Informe Resumen (.txt)");
        System.out.println("10. Cambiar de Base de Datos");
        System.out.println("---------------------------------------------");
        System.out.println("0. Salir");
        System.out.print("Elige una opción: ");
    }

    /**
     * Lógica central que llama al Service según la opción elegida.
     * @param opcion La opción del usuario.
     * @param esMySQL El estado actual de la BBDD.
     */
    // --- CORREGIDO: Ahora devuelve void ---
    private static void ejecutarOpcion(int opcion, boolean esMySQL) {

        // Este bloque 'try-catch' general atrapa todos los errores de las operaciones
        // y evita que el programa se cierre.
        try {
            switch (opcion) {
                case 1: // Registrar Propietario
                    System.out.println("--- Registrar Propietario ---");
                    String dni = pedirString("DNI:");
                    String nombre = pedirString("Nombre:");
                    String apellidos = pedirString("Apellidos:");
                    String tel = pedirStringOpcional("Teléfono (opcional, pulsa ENTER para omitir):");
                    gestor.insertarPropietario(esMySQL, dni, nombre, apellidos, tel);
                    System.out.println("Propietario registrado con éxito.");
                    break;

                case 2: // Insertar Coche
                    System.out.println("--- Insertar Coche ---");
                    String mat = pedirString("Matrícula:");
                    String marca = pedirString("Marca:");
                    String modelo = pedirString("Modelo:");
                    List<String> extras = pedirExtras();
                    double precio = pedirDouble("Precio:");
                    gestor.insertarCoche(mat, marca, modelo, extras, precio, esMySQL);
                    System.out.println("Coche insertado con éxito.");
                    break;

                case 3: // Listar Coches (Concesionario)
                    System.out.println("--- Coches del Concesionario (Sin Propietario) ---");
                    List<Coche> cochesSinDueno = gestor.listarCoches(esMySQL, false);
                    if (cochesSinDueno.isEmpty()) {
                        System.out.println("No hay coches sin propietario.");
                    } else {
                        cochesSinDueno.forEach(System.out::println);
                    }
                    break;

                case 4: // Listar Coches (Propietarios)
                    System.out.println("--- Coches Vendidos (Con Propietario) ---");
                    List<Coche> cochesConDueno = gestor.listarCoches(esMySQL, true);
                    if (cochesConDueno.isEmpty()) {
                        System.out.println("No hay coches con propietario.");
                    } else {
                        cochesConDueno.forEach(System.out::println);
                    }
                    break;

                case 5: // Modificar Coche
                    System.out.println("--- Modificar Coche ---");
                    String matMod = pedirString("Matrícula del coche a modificar:");
                    System.out.println("(Deja en blanco los campos que no quieras modificar)");
                    String marcaMod = pedirStringOpcional("Nueva Marca:"); // Usamos opcional
                    String modeloMod = pedirStringOpcional("Nuevo Modelo:"); // Usamos opcional
                    List<String> extrasMod = pedirExtras();
                    double precioMod = pedirDouble("Nuevo Precio (o -1 para no cambiar):");
                    gestor.modificarCoche(matMod, marcaMod, modeloMod, extrasMod, precioMod, esMySQL);
                    System.out.println("Coche modificado con éxito.");
                    break;

                case 6: // Borrar Coche
                    System.out.println("--- Borrar Coche ---");
                    String matBorrar = pedirString("Matrícula del coche a borrar:");
                    gestor.borrarCoche(matBorrar, esMySQL);
                    System.out.println("Coche borrado con éxito.");
                    break;

                case 7: // Realizar Traspaso
                    System.out.println("--- Realizar Traspaso ---");
                    String matTrasp = pedirString("Matrícula del coche a traspasar:");
                    String dniComprador = pedirString("DNI del comprador:");
                    double monto = pedirDouble("Monto económico:");
                    gestor.traspaso(matTrasp, dniComprador, monto, esMySQL);
                    System.out.println("Traspaso realizado con éxito.");
                    break;

                case 8: // Procedimiento Almacenado (Solo MySQL)
                    if (esMySQL) {
                        System.out.println("--- Buscar Coches por Marca (Proc. Almacenado) ---");
                        String marcaSP = pedirString("Marca a buscar:");
                        List<Coche> cochesSP = gestor.procedimientoAlmacenado(marcaSP);
                        if (cochesSP.isEmpty()) {
                            System.out.println("No se encontraron coches de la marca '" + marcaSP + "'.");
                        } else {
                            cochesSP.forEach(System.out::println);
                        }
                    } else {
                        System.out.println("Opción no válida (solo disponible para MySQL).");
                    }
                    break;

                case 9: // Generar Resumen
                    System.out.println("Generando informe...");
                    gestor.generarResumen(esMySQL);
                    System.out.println("Informe 'report/informe_concesionario.txt' generado.");
                    break;

                // --- CORREGIDO: Casos 0 y 10 eliminados de aquí ---

                default:
                    // Si la opción era 8 pero estamos en SQLite, ya se manejó arriba.
                    // Si es cualquier otro número, es inválido.
                    if (!(opcion == 8 && !esMySQL)) {
                        System.out.println("Opción no válida.");
                    }
            }

            // --- MANEJO DE EXCEPCIONES DE OPERACIÓN ---
        } catch (NumberFormatException e) {
            System.out.println("Error de entrada: El valor introducido no es un número válido.");
            System.out.println(e.getMessage());
        } catch (ConcesionarioExcepcion e) {
            System.out.println("Error de Lógica: " + e.getMessage());
        } catch (FileNotFoundException e) {
            System.out.println("Error de Fichero: No se encontró el archivo (quizás el CSV o el informe).");
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("Error de E/S (Fichero): " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error de Base de Datos (SQL): " + e.getMessage());
            System.out.println("(SQLState: " + e.getSQLState() + ", ErrorCode: " + e.getErrorCode() + ")");
        } catch (Exception e) {
            // Un 'catch' general para cualquier otro error inesperado (el padre)
            System.out.println("Error inesperado del sistema: " + e.getMessage());
            e.printStackTrace(); // Imprimimos el error completo
        }
    }


    // --- MÉTODOS AUXILIARES DE ENTRADA ---

    /**
     * Pide al usuario un String, asegurándose de que no esté vacío.
     */
    private static String pedirString(String mensaje) {
        String input;
        while (true) {
            System.out.print(mensaje + " ");
            input = sc.nextLine();
            if (input == null || input.isBlank()) {
                System.out.println("Error: El campo no puede estar vacío.");
            } else {
                return input.trim();
            }
        }
    }

    /**
     * Pide un String que SÍ puede estar vacío (para campos opcionales).
     */
    private static String pedirStringOpcional(String mensaje) {
        System.out.print(mensaje + " ");
        return sc.nextLine().trim();
    }

    /**
     * Pide al usuario un número (double), repitiendo si el formato es incorrecto.
     */
    private static double pedirDouble(String mensaje) {
        while (true) {
            System.out.print(mensaje + " ");
            try {
                return Double.parseDouble(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Error: Introduce un número decimal válido (ej. 19000.50).");
            }
        }
    }

    /**
     * Pide al usuario una lista de extras, uno por uno.
     */
    private static List<String> pedirExtras() {
        List<String> extras = new ArrayList<>();
        System.out.println("(Introduce los extras uno por uno. Escribe 'fin' para terminar)");
        while (true) {
            System.out.print("Añadir extra: ");
            String extra = sc.nextLine();
            if (extra.equalsIgnoreCase("fin")) {
                break;
            }
            if (!extra.isBlank()) {
                extras.add(extra.trim());
            }
        }
        return extras;
    }
}