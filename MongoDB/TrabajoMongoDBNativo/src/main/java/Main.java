import com.mongodb.MongoException;
import exception.TiendaException;
import service.GestorTienda;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GestorTienda gestor = null;

        System.out.println("🔌 Iniciando sistema...");

        try {
            // 1. Conexión inicial
            gestor = new GestorTienda();
            System.out.println("✅ Conexión establecida con MongoDB.");

            int opcion = -1;

            // 2. Bucle del Menú
            do {
                try {
                    System.out.println("\n--- 🎮 TIENDA GAMING MONGO DB ---");
                    System.out.println("1. Cargar Datos Semilla (Reset BBDD)");
                    System.out.println("2. Alta de Videojuego");
                    System.out.println("3. Alta de Cliente");
                    System.out.println("4. Realizar Venta (Transacción Manual)");
                    System.out.println("5. Ver Historial de Cliente");
                    System.out.println("6. Ver Ofertas (< 25€)");
                    System.out.println("0. Salir");
                    System.out.print("👉 Selecciona una opción: ");

                    // Leemos todo como texto y convertimos para evitar errores de buffer
                    String entrada = scanner.nextLine();
                    opcion = Integer.parseInt(entrada);

                    switch (opcion) {
                        case 1:
                            System.out.println("⏳ Borrando y cargando datos...");
                            gestor.cargarDatosSemilla();
                            System.out.println("✅ Datos cargados correctamente.");
                            break;

                        case 2:
                            System.out.println("\n--- NUEVO VIDEOJUEGO ---");
                            System.out.print("Título: ");
                            String titulo = scanner.nextLine();
                            System.out.print("Género: ");
                            String genero = scanner.nextLine();
                            System.out.print("Precio: ");
                            double precio = Double.parseDouble(scanner.nextLine());
                            System.out.print("Stock inicial: ");
                            int stock = Integer.parseInt(scanner.nextLine());

                            gestor.insertarJuego(titulo, genero, precio, stock);
                            System.out.println("✅ Videojuego guardado.");
                            break;

                        case 3:
                            System.out.println("\n--- NUEVO CLIENTE ---");
                            System.out.print("Nombre: ");
                            String nombre = scanner.nextLine();
                            System.out.print("Email: ");
                            String email = scanner.nextLine();

                            gestor.insertarCliente(nombre, email);
                            System.out.println("✅ Cliente registrado.");
                            break;

                        case 4:
                            System.out.println("\n--- REALIZAR VENTA ---");
                            System.out.print("Email del Cliente: ");
                            String emailVenta = scanner.nextLine();
                            System.out.print("Título del Videojuego: ");
                            String tituloVenta = scanner.nextLine();

                            gestor.realizarVenta(emailVenta, tituloVenta);
                            System.out.println("✅ Venta realizada con éxito (Stock actualizado).");
                            break;

                        case 5:
                            System.out.println("\n--- HISTORIAL DE COMPRAS ---");
                            System.out.print("Introduce el email del cliente: ");
                            String emailHistorial = scanner.nextLine();

                            List<String> historial = gestor.mostrarHistoralCliente(emailHistorial);

                            // GestorTienda ya devuelve una lista con "No hay datos" si está vacía,
                            // así que la imprimimos directamente.
                            System.out.println("Juegos comprados:");
                            for (String juego : historial) {
                                System.out.println(" - " + juego);
                            }
                            break;

                        case 6:
                            System.out.println("\n--- OFERTAS DISPONIBLES (< 25€) ---");
                            List<Map.Entry<String, Double>> ofertas = gestor.mostrarJuegosMenor25();

                            // Requisito: Si la lista está vacía, imprimir mensaje
                            if (ofertas.isEmpty()) {
                                System.out.println("ℹ️ No hay datos (No hay ofertas disponibles actualmente).");
                            } else {
                                System.out.printf("%-30s %-10s%n", "TÍTULO", "PRECIO");
                                System.out.println("------------------------------------------");
                                for (Map.Entry<String, Double> entry : ofertas) {
                                    System.out.printf("%-30s %.2f €%n", entry.getKey(), entry.getValue());
                                }
                            }
                            break;

                        case 0:
                            System.out.println("👋 ¡Hasta la próxima!");
                            break;

                        default:
                            System.out.println("⚠️ Opción no válida.");
                    }

                } catch (NumberFormatException e) {
                    System.err.println("❌ Error de formato: Debes introducir un número válido.");
                } catch (TiendaException e) {
                    // Excepciones de negocio (Lógica controlada: Precio negativo, sin stock, etc.)
                    System.err.println("🚫 Operación denegada: " + e.getMessage());
                } catch (MongoException e) {
                    // Excepciones de Base de Datos
                    System.err.println("🔥 Error de Base de Datos: " + e.getMessage());
                } catch (Exception e) {
                    // Cualquier otro error inesperado (NullPointer, etc.)
                    System.err.println("❌ Error inesperado: " + e.getMessage());
                    // e.printStackTrace(); // Descomenta si necesitas depurar
                }
            } while (opcion != 0);

        } catch (IOException | MongoException e) {
            System.err.println("❌ Error CRÍTICO al conectar/iniciar: " + e.getMessage());
        } finally {
            // 3. Cerrar recursos siempre al final
            if (gestor != null) {
                System.out.println("🔌 Cerrando conexión con MongoDB...");
                gestor.cerrarConexion();
            }
            scanner.close();
        }
    }
}