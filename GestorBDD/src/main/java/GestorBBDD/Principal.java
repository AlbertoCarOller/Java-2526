package GestorBBDD;

import java.io.IOException;
import java.util.Scanner;

public class Principal {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            // Indicamos la ruta donde se ubica la bbdd
            String rutaFicheroDat = "C:\\Users\\Alberto.DESKTOP-O1GC77M\\Desktop\\Java\\" +
                    "Java-2526\\GestorBDD\\src\\main\\java\\GestorBBDD\\base_de_datos.dat";
            // Indicamos la ruta donde se encuentra el CSV
            String rutaCSV = "C:\\Users\\Alberto.DESKTOP-O1GC77M\\Desktop\\Java\\Java-2526\\GestorBDD\\" +
                    "src\\main\\java\\GestorBBDD\\BBDD Coches.csv";
            // Creamos el gestor de bbdd con las rutas creadas anteriormente
            GestorBBDD gestorBBDD = new GestorBBDD(rutaFicheroDat, rutaCSV);
            // Llamamos al menú principal
            menuPrincipal(gestorBBDD);

        } catch (IOException | GestorBBDDException | RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Esta función básicamente es el menú, permite al usuario
     * elegir entre todas las opciones disponibles
     *
     * @param gestorBBDD el gestor de la bbdd
     * @throws GestorBBDDException
     * @throws IOException
     */
    public static void menuPrincipal(GestorBBDD gestorBBDD) throws GestorBBDDException, IOException {
        int opcion;
        do {
            // Mostramos por pantalla el menú
            System.out.println("-----------Registros-----------\n" + mostrarRegistrosPosiciones(gestorBBDD) + "\n" + """
                    -----------Menú principal-----------
                    1. Insertar registro
                    2. Borrar registro
                    3. Modificar registro
                    4. Ordenar por matrícula
                    5. Cargar CSV
                    6. Salir
                    
                    -Elige opción:""");
            opcion = Integer.parseInt(sc.nextLine());
            // Dependiendo de la opción, ejecutaremos una función u otra
            switch (opcion) {
                // Insertar registro
                case 1 -> {
                    // Solicitamos la matrícula
                    System.out.println("Introduce la matricula:");
                    String matricula = sc.nextLine();
                    // Solicitamos la marca
                    System.out.println("Introduce la marca:");
                    String marca = sc.nextLine();
                    // Solicitamos el modelo
                    System.out.println("Introduce el modelo:");
                    String modelo = sc.nextLine();
                    // Solicitamos la posición hasta que sea válida
                    System.out.println("Introduce la posicion:");
                    long posicionL = comprobarPosicionLong(sc.nextLine());
                    // Llamamos a la función
                    System.out.println(gestorBBDD.insertarRegistro(matricula, marca, modelo, posicionL));
                }
                // Borrar registro
                case 2 -> {
                    System.out.println("""
                            1. Borrar registro por matrícula
                            2. Borrar registro por posición
                            
                            Elige una opción""");
                    int opcion2 = Integer.parseInt(sc.nextLine());
                    switch (opcion2) {
                        // Borrar registro por matrícula
                        case 1 -> {
                            System.out.println("Introduce la matrícula:");
                            String matricula = sc.nextLine();
                            System.out.println(gestorBBDD.borrarRegistroMatricula(matricula));
                        }
                        // Borrar registro por posición
                        case 2 -> {
                            System.out.println("Introduce la posicion:");
                            System.out.println(gestorBBDD.borrarRegistroPorPosicion(comprobarPosicionLong(sc.nextLine())));
                        }
                        // En caso de no elegir ninguna opción válida
                        default -> System.out.println("Opción inválida");
                    }
                }
                // Modificar registro
                case 3 -> {
                    System.out.println("Introduce la posición:");
                    long posicionL = comprobarPosicionLong(sc.nextLine());
                    System.out.println("Introduce la marca:");
                    String marca = sc.nextLine();
                    System.out.println("Introduce el modelo:");
                    String modelo = sc.nextLine();
                    System.out.println(gestorBBDD.modificarRegistro(posicionL, marca, modelo));
                }
                // Ordenar por matrícula
                case 4 -> System.out.println(gestorBBDD.ordenarPorMatricula());
                // Cargar en CSV
                case 5 -> {
                    System.out.println("Elija la posicion:");
                    long posicionL = comprobarPosicionLong(sc.nextLine());
                    try {
                        System.out.println(gestorBBDD.cargarCSV(posicionL));
                    } catch (GestorBBDDException e) {
                        throw new RuntimeException(e);
                    }
                }
                // Salir
                case 6 -> System.out.println("Hasta pronto");
                // Opción inválida
                default -> System.out.println("Opción invalida");
            }
        } while (opcion != 6);
    }

    /**
     * Esta función va a comprobar si la posición está formada por números
     * o no, en caso de que no solicitará indefinidamente
     *
     * @param posicion la posición a comprobar
     * @return la posición ya en long
     */
    public static Long comprobarPosicionLong(String posicion) {
        while (!posicion.matches("^[0-9]+$")) {
            System.out.println("Introduce la posición:");
            posicion = sc.nextLine();
        }
        return Long.parseLong(posicion);
    }

    /**
     * Esta función va a mostrar las posiciones
     * donde hay registros, de la 0 a la x
     *
     * @param gestorBBDD el gestor de bbdd
     * @return los registros disponibles en posiciones
     */
    public static String mostrarRegistrosPosiciones(GestorBBDD gestorBBDD) {
        String RegistrosDisponibles = "Registros disponibles: No hay registros";
        if (gestorBBDD.getTotalRegistros() > 0) {
            RegistrosDisponibles = "Registros disponibles: (0-" + (gestorBBDD.getTotalRegistros() - 1) + ")";
        }
        return RegistrosDisponibles;
    }
}