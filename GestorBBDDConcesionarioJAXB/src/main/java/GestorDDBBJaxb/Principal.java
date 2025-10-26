package GestorDDBBJaxb;

import jakarta.xml.bind.JAXBException;

import java.io.FileNotFoundException; // Importamos la hija
import java.io.IOException;           // Importamos la padre
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Principal {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        GestorDDBBJaxb gestor = null;

        // --- BLOQUE DE ARRANQUE ---
        // Este try-catch es el único que, si falla, cierra el programa (con 'return'),
        // ya que el menú no puede funcionar si el gestor no se inicializa.
        try {
            gestor = new GestorDDBBJaxb();
            System.out.println("Gestor de Concesionario iniciado correctamente.");

            // --- Captura Jerárquica (Arranque) ---
        } catch (FileNotFoundException e) { // 1. Hija de IOException
            System.out.println("Error CRÍTICO (Archivo no encontrado): No se encuentra 'config.properties' o el XML de BBDD.");
            System.out.println("Ruta: " + e.getMessage());
            return; // Salimos
        } catch (IOException e) { // 2. Padre
            System.out.println("Error CRÍTICO de E/S al iniciar.");
            System.out.println(e.getMessage());
            return; // Salimos
        } catch (JAXBException e) { // 3. "Hermana" (otra rama de Exception)
            System.out.println("Error CRÍTICO de JAXB al iniciar: Problema con las anotaciones XML o el contexto.");
            System.out.println(e.getMessage());
            return; // Salimos
        } catch (GestorBBDDJaxbExcepcion e) { // 4. "Hermana" (Excepción personalizada)
            System.out.println("Error CRÍTICO de Lógica al iniciar: " + e.getMessage());
            return; // Salimos
        } catch (Exception e) { // 5. "Abuelo" (Cualquier otro error inesperado)
            System.out.println("Error CRÍTICO INESPERADO al arrancar: " + e.getMessage());
            e.printStackTrace(); // Imprimimos el error completo
            return; // Salimos
        }

        // --- BUCLE PRINCIPAL DEL MENÚ ---
        boolean salir = false;
        while (!salir) {
            mostrarMenuPrincipal();

            int opcion;
            try {
                opcion = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) { // Excepción hija (de RuntimeException)
                System.out.println("Error: Introduce un número válido.");
                continue; // Vuelve al inicio del while (no ejecuta el switch)
            }

            // Dentro del switch, cada 'case' tiene su propio try-catch.
            // Esto asegura que si una operación (ej. "Añadir Coche") falla,
            // el 'catch' maneja el error y el 'while' principal continúa.
            switch (opcion) {
                case 1:
                    // --- AÑADIR COCHE ---
                    try {
                        // ... (código para pedir datos de coche)
                        System.out.println("--- Añadir Coche Nuevo ---");
                        System.out.print("Matrícula: ");
                        String mat = sc.nextLine();
                        System.out.print("Marca: ");
                        String marca = sc.nextLine();
                        System.out.print("Modelo: ");
                        String mod = sc.nextLine();

                        List<String> extras = new ArrayList<>();
                        while (true) {
                            System.out.print("Añadir extra (o 'fin' para terminar): ");
                            String extra = sc.nextLine();
                            if (extra.equalsIgnoreCase("fin")) {
                                break;
                            }
                            if (!extra.isBlank()) {
                                extras.add(extra);
                            }
                        }

                        Coche c = new Coche(mat, marca, mod, extras);
                        gestor.agregarCoche(c);
                        System.out.println("Coche añadido con éxito (ID: " + c.getId() + "). BBDD actualizada.");

                        // --- Captura Jerárquica (Operación) ---
                    } catch (GestorBBDDJaxbExcepcion e) { // 1. Específica (Lógica)
                        System.out.println("Error al añadir coche (Lógica): " + e.getMessage());
                    } catch (FileNotFoundException e) { // 2. Específica (Hija de IO)
                        System.out.println("Error al añadir (Archivo no encontrado): No se pudo escribir el XML.");
                        System.out.println(e.getMessage());
                    } catch (IOException e) { // 3. Padre (IO)
                        System.out.println("Error al añadir (E/S): " + e.getMessage());
                    } catch (JAXBException e) { // 4. Específica (XML)
                        System.out.println("Error al añadir (JAXB): No se pudo convertir a XML.");
                        System.out.println(e.getMessage());
                    } catch (Exception e) { // 5. Padre (General)
                        System.out.println("Error inesperado al añadir: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break; // Sale del 'switch'

                case 2:
                    // --- IMPORTAR DESDE CSV ---
                    try {
                        System.out.println("Importando coches desde CSV...");
                        gestor.importarCocheCSV();

                    } catch (FileNotFoundException e) { // 1. Hija (No encuentra el CSV o el XML)
                        System.out.println("Error al importar CSV (Archivo no encontrado): Verifique la ruta del CSV o XML.");
                        System.out.println(e.getMessage());
                    } catch (IOException e) { // 2. Padre
                        System.out.println("Error al importar CSV (E/S): " + e.getMessage());
                    } catch (JAXBException e) { // 3. Hermana
                        System.out.println("Error al importar CSV (JAXB): No se pudo guardar en XML.");
                        System.out.println(e.getMessage());
                    } catch (Exception e) { // 4. Padre General
                        System.out.println("Error inesperado al importar CSV: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case 3:
                    // --- ELIMINAR COCHE ---
                    try {
                        System.out.println("--- Eliminar Coche ---");
                        System.out.print("Matrícula del coche a eliminar: ");
                        String mat = sc.nextLine();
                        gestor.eliminarCoche(mat);
                        System.out.println("Coche con matrícula " + mat + " eliminado.");

                    } catch (GestorBBDDJaxbExcepcion e) { // 1. Lógica (ej. "No se ha encontrado el coche")
                        System.out.println("Error al eliminar coche (Lógica): " + e.getMessage());
                    } catch (FileNotFoundException e) { // 2. Hija
                        System.out.println("Error al eliminar (Archivo no encontrado): No se pudo guardar el XML.");
                        System.out.println(e.getMessage());
                    } catch (IOException e) { // 3. Padre
                        System.out.println("Error al eliminar (E/S): No se pudo guardar el XML.");
                        System.out.println(e.getMessage());
                    } catch (JAXBException e) { // 4. Hermana
                        System.out.println("Error al eliminar (JAXB): No se pudo guardar el XML.");
                        System.out.println(e.getMessage());
                    } catch (Exception e) { // 5. General
                        System.out.println("Error inesperado al eliminar: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case 4:
                    // --- SUBMENÚ MODIFICAR ---
                    manejarSubMenuModificar(gestor, sc);
                    break;

                case 5:
                    // --- ORDENAR POR MATRÍCULA ---
                    try {
                        gestor.ordenarPorMatricula();
                        System.out.println("Concesionario ordenado por matrícula en el XML.");

                    } catch (FileNotFoundException e) { // 1. Hija
                        System.out.println("Error al ordenar (Archivo no encontrado): No se pudo guardar el XML.");
                        System.out.println(e.getMessage());
                    } catch (IOException e) { // 2. Padre
                        System.out.println("Error al ordenar (E/S): " + e.getMessage());
                    } catch (JAXBException e) { // 3. Hermana
                        System.out.println("Error al ordenar (JAXB): " + e.getMessage());
                    } catch (Exception e) { // 4. General
                        System.out.println("Error inesperado al ordenar: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case 6:
                    // --- SUBMENÚ EXPORTAR JSON ---
                    manejarSubMenuExportarJSON(gestor, sc);
                    break;

                case 7:
                    // --- SUBMENÚ IMPORTAR JSON ---
                    manejarSubMenuImportarJSON(gestor, sc);
                    break;

                case 8:
                    // --- GENERAR RESUMEN ---
                    try {
                        gestor.realizarResumen();
                        System.out.println("Resumen 'informe_concesionario.txt' generado/actualizado.");

                    } catch (FileNotFoundException e) { // 1. Hija
                        System.out.println("Error al generar resumen (Ruta no válida): " + e.getMessage());
                    } catch (IOException e) { // 2. Padre
                        System.out.println("Error al generar el resumen (E/S): " + e.getMessage());
                    } catch (Exception e) { // 3. General
                        System.out.println("Error inesperado al generar resumen: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;

                case 0:
                    salir = true;
                    System.out.println("Saliendo del programa...");
                    break;

                default:
                    System.out.println("Opción no válida. Introduce un número del 0 al 8.");
            }

            if (!salir) {
                System.out.println("\n(Pulsa ENTER para continuar...)");
                sc.nextLine();
            }
        }

        sc.close();
    }

    /**
     * Esta función va a imprimir poe pantalla las opciones
     * del menú principal
     */
    private static void mostrarMenuPrincipal() {
        System.out.println("\n--- GESTOR DE CONCESIONARIO (JAXB) ---");
        System.out.println("1. Añadir Coche");
        System.out.println("2. Importar Coches desde CSV");
        System.out.println("3. Eliminar Coche (por matrícula)");
        System.out.println("4. Modificar Coche");
        System.out.println("5. Ordenar Concesionario por Matrícula");
        System.out.println("6. Exportar a JSON");
        System.out.println("7. Importar desde JSON");
        System.out.println("8. Generar Resumen/Informe");
        System.out.println("0. Salir");
        System.out.print("Elige una opción: ");
    }


    /**
     * Esta función va a mostrar un submenú para modificar
     * el campo de un coche, teniendo en cuentas las excepciones
     * correspondientes
     * @param gestor el gestor
     * @param sc el Scanner
     */
    private static void manejarSubMenuModificar(GestorDDBBJaxb gestor, Scanner sc) {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- Submenú Modificar Coche ---");
            System.out.println("1. Modificar Marca");
            System.out.println("2. Modificar Modelo");
            System.out.println("3. Modificar un Extra");
            System.out.println("0. Volver al menú principal");
            System.out.print("Elige una opción: ");

            // Controlamos que la opción sea un número
            int opcionMod;
            try {
                opcionMod = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Error: Introduce un número válido.");
                continue;
            }

            String mat;
            try {
                switch (opcionMod) {
                    case 1:
                        System.out.print("Matrícula del coche a modificar: ");
                        mat = sc.nextLine();
                        System.out.print("Nueva Marca: ");
                        String marca = sc.nextLine();
                        gestor.modificarMarca(mat, marca);
                        System.out.println("Marca modificada.");
                        break;
                    case 2:
                        System.out.print("Matrícula del coche a modificar: ");
                        mat = sc.nextLine();
                        System.out.print("Nuevo Modelo: ");
                        String modelo = sc.nextLine();
                        gestor.modificarModelo(mat, modelo);
                        System.out.println("Modelo modificado.");
                        break;
                    case 3:
                        System.out.print("Matrícula del coche a modificar: ");
                        mat = sc.nextLine();
                        System.out.print("Índice del extra a modificar (empezando en 1): ");
                        int indice = Integer.parseInt(sc.nextLine()) - 1;
                        System.out.print("Nuevo valor del extra: ");
                        String extra = sc.nextLine();
                        gestor.modificarExtra(mat, extra, indice);
                        System.out.println("Extra modificado.");
                        break;
                    case 0:
                        volver = true;
                        break;
                    default:
                        System.out.println("Opción no válida.");
                }

                // --- Captura Jerárquica (Submenú Modificar) ---
                // 1. Hijas (Runtime) - Específicas de la lógica del menú
            } catch (NumberFormatException e) {
                System.out.println("Error: El índice debe ser un número.");
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Error: El índice del extra no existe.");
                // 2. Específica (Lógica)
            } catch (GestorBBDDJaxbExcepcion e) {
                System.out.println("Error al modificar (Lógica): " + e.getMessage());
                // 3. Hija (IO)
            } catch (FileNotFoundException e) {
                System.out.println("Error al modificar (Archivo no encontrado): No se pudo guardar el XML.");
                System.out.println(e.getMessage());
                // 4. Padre (IO)
            } catch (IOException e) {
                System.out.println("Error al modificar (E/S): " + e.getMessage());
                // 5. Hermana (XML)
            } catch (JAXBException e) {
                System.out.println("Error al modificar (JAXB): " + e.getMessage());
                // 6. General (Padre)
            } catch (Exception e) {
                System.out.println("Error inesperado al modificar: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Esta función va a mostrar el submenú para exportar a JSON,
     * controlando las excepciones y llamando a las funciones
     * correspondientes
     * @param gestor el Gestor
     * @param sc el Scanner
     */
    private static void manejarSubMenuExportarJSON(GestorDDBBJaxb gestor, Scanner sc) {
        boolean volver = false;
        while (!volver) {
            // ... (código del menú) ...
            System.out.println("\n--- Submenú Exportar a JSON ---");
            System.out.println("1. Exportar Concesionario Completo");
            System.out.println("2. Exportar un Coche Específico (por matrícula)");
            System.out.println("0. Volver al menú principal");
            System.out.print("Elige una opción: ");

            int opcionExp;
            try {
                opcionExp = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Error: Introduce un número válido.");
                continue;
            }

            try {
                switch (opcionExp) {
                    case 1:
                        gestor.pasarJSON(false, null);
                        System.out.println("Concesionario completo exportado a JSON.");
                        break;
                    case 2:
                        System.out.print("Matrícula del coche a exportar: ");
                        String mat = sc.nextLine();
                        gestor.pasarJSON(true, mat);
                        System.out.println("Coche exportado a JSON.");
                        break;
                    case 0:
                        volver = true;
                        break;
                    default:
                        System.out.println("Opción no válida.");
                }

                // --- Captura Jerárquica ---
            } catch (GestorBBDDJaxbExcepcion e) { // 1. Lógica
                System.out.println("Error al exportar a JSON (Lógica): " + e.getMessage());
            } catch (FileNotFoundException e) { // 2. Hija (IO)
                System.out.println("Error al exportar a JSON (Ruta no válida): " + e.getMessage());
            } catch (IOException e) { // 3. Padre (IO)
                System.out.println("Error al exportar a JSON (E/S): " + e.getMessage());
            } catch (Exception e) { // 4. General
                System.out.println("Error inesperado al exportar: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Esta función va a mostrar las opciones de la importación con JSON,
     * va a controlar las excepciones, llamando a las funciones necesarias
     * @param gestor el Gestor
     * @param sc el Scanner
     */
    private static void manejarSubMenuImportarJSON(GestorDDBBJaxb gestor, Scanner sc) {
        boolean volver = false;
        while (!volver) {
            // ... (código del menú) ...
            System.out.println("\n--- Submenú Importar desde JSON ---");
            System.out.println("1. Importar Concesionario Completo (SOBRESCRiBE BBDD)");
            System.out.println("2. Importar y Añadir un solo Coche");
            System.out.println("0. Volver al menú principal");
            System.out.print("Elige una opción: ");

            int opcionImp;
            try {
                opcionImp = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Error: Introduce un número válido.");
                continue;
            }

            try {
                switch (opcionImp) {
                    case 1:
                        gestor.importarJSON(true);
                        System.out.println("Concesionario completo importado. BBDD actualizada.");
                        break;
                    case 2:
                        gestor.importarJSON(false);
                        System.out.println("Coche importado y añadido a la BBDD.");
                        break;
                    case 0:
                        volver = true;
                        break;
                    default:
                        System.out.println("Opción no válida.");
                }

                // --- Captura Jerárquica ---
            } catch (GestorBBDDJaxbExcepcion e) { // 1. Lógica
                System.out.println("Error al importar desde JSON (Lógica): " + e.getMessage());
            } catch (FileNotFoundException e) { // 2. Hija (IO)
                System.out.println("Error al importar (Archivo no encontrado): No se encuentra el JSON o el XML de destino.");
                System.out.println(e.getMessage());
            } catch (IOException e) { // 3. Padre (IO)
                System.out.println("Error al importar desde JSON (E/S): " + e.getMessage());
            } catch (JAXBException e) { // 4. Hermana (XML)
                System.out.println("Error al importar desde JSON (JAXB): No se pudo guardar el XML.");
                System.out.println(e.getMessage());
            } catch (Exception e) { // 5. General
                System.out.println("Error inesperado al importar: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}