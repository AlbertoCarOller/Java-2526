package GestorDDBBJaxb;

import jakarta.xml.bind.JAXBException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Principal {
    public static void main(String[] args) {
        // Inicializamos el Scanner fuera del bucle
        Scanner sc = new Scanner(System.in);
        GestorDDBBJaxb gestor = null;

        try {
            // 1. INTENTAMOS INICIAR EL GESTOR
            // Si esto falla (ej. no encuentra .properties), el programa no puede continuar.
            gestor = new GestorDDBBJaxb();
            System.out.println("Gestor de Concesionario iniciado correctamente.");

        } catch (IOException | GestorBBDDJaxbExcepcion | JAXBException e) {
            System.out.println("Error CRÍTICO al iniciar el gestor: " + e.getMessage());
            // Si el gestor no se puede crear, salimos.
            return;
        }

        // 2. BUCLE PRINCIPAL DEL MENÚ
        boolean salir = false;
        while (!salir) {
            mostrarMenuPrincipal();
            int opcion;
            try {
                opcion = Integer.parseInt(sc.nextLine()); // Leemos como String y parseamos para evitar errores
            } catch (NumberFormatException e) {
                System.out.println("Error: Introduce un número válido.");
                continue; // Vuelve al inicio del bucle while
            }

            switch (opcion) {
                case 1:
                    // --- AÑADIR COCHE ---
                    try {
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
                            extras.add(extra);
                        }
                        // Creamos el coche (el ID se autogenera en el constructor de Coche)
                        Coche c = new Coche(mat, marca, mod, extras);
                        gestor.agregarCoche(c);
                        System.out.println("Coche añadido con éxito (ID: " + c.getId() + "). BBDD actualizada.");

                    } catch (Exception e) {
                        System.out.println("Error al añadir coche: " + e.getMessage());
                    }
                    break;
                case 2:
                    // --- IMPORTAR DESDE CSV ---
                    try {
                        System.out.println("Importando coches desde CSV...");
                        int importados = gestor.importarCocheCSV();
                        System.out.println(importados + " coches nuevos importados desde CSV.");
                    } catch (Exception e) {
                        System.out.println("Error al importar CSV: " + e.getMessage());
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
                    } catch (Exception e) {
                        System.out.println("Error al eliminar coche: " + e.getMessage());
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
                    } catch (Exception e) {
                        System.out.println("Error al ordenar: " + e.getMessage());
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
                    } catch (Exception e) {
                        System.out.println("Error al generar el resumen: " + e.getMessage());
                    }
                    break;
                case 0:
                    salir = true;
                    System.out.println("Saliendo del programa...");
                    break;
                default:
                    System.out.println("Opción no válida. Introduce un número del 0 al 8.");
            }
            // Pequeña pausa
            if (!salir) {
                System.out.println("\n(Pulsa ENTER para continuar...)");
                sc.nextLine();
            }
        }
        // Cerramos el Scanner al salir del bucle
        sc.close();
    }

    /**
     * Muestra el menú principal en la consola.
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
     * Gestiona el submenú para modificar campos de un coche.
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

            int opcionMod;
            try {
                opcionMod = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Error: Introduce un número válido.");
                continue;
            }

            String mat; // Pedimos la matrícula dentro de cada caso
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
                        int indice = Integer.parseInt(sc.nextLine()) - 1; // Convertimos a índice 0
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
            } catch (Exception e) {
                System.out.println("Error al modificar: " + e.getMessage());
            }
        }
    }

    /**
     * Gestiona el submenú para exportar a JSON.
     */
    private static void manejarSubMenuExportarJSON(GestorDDBBJaxb gestor, Scanner sc) {
        boolean volver = false;
        while (!volver) {
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
            } catch (Exception e) {
                System.out.println("Error al exportar a JSON: " + e.getMessage());
            }
        }
    }

    /**
     * Gestiona el submenú para importar desde JSON.
     */
    private static void manejarSubMenuImportarJSON(GestorDDBBJaxb gestor, Scanner sc) {
        boolean volver = false;
        while (!volver) {
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
            } catch (Exception e) {
                System.out.println("Error al importar desde JSON: " + e.getMessage());
            }
        }
    }
}