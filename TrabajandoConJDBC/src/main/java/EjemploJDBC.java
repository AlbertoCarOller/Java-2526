import model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EjemploJDBC {

    // --- Constantes de Conexión ---
    // Cambia estos valores para que coincidan con tu configuración de MySQL
    private static final String URL = "jdbc:mysql://localhost:3307/mi_bbdd_demo?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "root"; // <--- ¡¡¡CAMBIAR ESTO!!!

    /**
     * Método principal que orquesta todas las demostraciones en orden.
     */
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        int opcion = -1;

        do {
            System.out.println("\n--- MENÚ DE DEMOSTRACIONES JDBC ---");
            System.out.println("1. (DDL) Crear/Reiniciar Tablas");
            System.out.println("2. (DML) Insertar con PreparedStatement");
            System.out.println("3. (DQL) Consultar y Mapear a POJOs");
            System.out.println("4. (Batch) Inserción por Lotes");
            System.out.println("5. (Transacción) Demostración de Commit/Rollback");
            System.out.println("6. (Callable) Llamar a Stored Procedure");
            System.out.println("-------------------------------------");
            System.out.println("0. Salir");
            System.out.print("Selecciona una opción: ");

            try {
                // Leemos la línea completa y la convertimos
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                opcion = -1; // Fuerza el default en el switch
            }

            switch (opcion) {
                case 1:
                    System.out.println("\n--- Demo 1: DDL con Statement ---");
                    demoDDL();
                    break;
                case 2:
                    System.out.println("\n--- Demo 2: DML con PreparedStatement ---");
                    demoInsertPreparedStatement();
                    break;
                case 3:
                    System.out.println("\n--- Demo 3: DQL y Mapeo a POJOs ---");
                    demoSelectConMapeo();
                    break;
                case 4:
                    System.out.println("\n--- Demo 4: Ejecución por Lotes (Batch) ---");
                    demoBatchInsert();
                    break;
                case 5:
                    System.out.println("\n--- Demo 5: Gestión de Transacciones ---");
                    demoTransaccion();
                    break;
                case 6:
                    System.out.println("\n--- Demo 6: CallableStatement (Stored Procedure) ---");
                    demoCallableStatement();
                    break;
                case 0:
                    System.out.println("\nSaliendo...");
                    break;
                default:
                    System.out.println("Opción no válida. Inténtalo de nuevo.");
            }

        } while (opcion != 0);

        scanner.close();
        System.out.println("\n--- Fin de las demostraciones ---");
    }

    private static void demoCallableStatement() {
        String sqlCall = "{CALL sp_get_usuario_info(?, ?, ?)}";
        int idBuscado = 1;
        try(Connection connection = DriverManager.getConnection(URL, USER, PASS);
            CallableStatement callableStatement = connection.prepareCall(sqlCall)) {

            callableStatement.setInt(1, idBuscado);
            callableStatement.registerOutParameter(2, Types.VARCHAR);
            callableStatement.registerOutParameter(3, Types.VARCHAR);

            callableStatement.execute();

            String nombre = callableStatement.getString(2);
            String email = callableStatement.getString(3);

            System.out.println("Datos recuperados por el SP");
            System.out.println("Nombre: " + nombre);
            System.out.println("Email: " + email);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void demoTransaccion() {
        String sqlRestaA = "UPDATE cuentas SET saldo = saldo - 100 WHERE id = 'A'";
        //String sqlSumarB = "UPDATE cuentas SET saldoInexistente = saldo + 100 WHERE id = 'B'";
        String sqlSumarB = "UPDATE cuentas SET saldo = saldo + 100 WHERE id = 'B'";

        // Al hacer una transacción la conexión debe de ir fuera del try
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASS);
            conn.setAutoCommit(false);

            try(Statement statement = conn.createStatement()) {
                statement.executeUpdate(sqlRestaA);
                statement.executeUpdate(sqlSumarB);
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // En caso de que haya algún problema,no se completa la trasancción
                } catch (SQLException ex) {
                    System.err.println(e.getMessage());
                }
            }
            // Se crea un bloque finally porque no es un try.with-resources y debe cerrarse de alguna forma los recursos
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Volvemos a poner auto-commit a true
                    conn.close(); // Cerramos la conexión
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    private static void demoBatchInsert() {
        String sql = "INSERT INTO usuarios(nombre, email) VALUES (?, ?)";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASS);
            try(PreparedStatement pstmt = conn.prepareStatement(sql)) {
                System.out.println("Añadiendo al lote...");
                for (int i = 0; i < 5; i++) {
                    pstmt.setString(1, "Usuario Batch " + i);
                    pstmt.setString(2, "batch" + i + "@email.com");
                    // Añadimos sentencias la Batch
                    pstmt.addBatch(); // Añade la sentencia al lote
                }
                // Cuando se hace un Batch se debe de desactivar el auto-commit
                conn.setAutoCommit(false);

                // 3. Ejecutamos el lote
                int[] resultados = pstmt.executeBatch();
                conn.commit();
                System.out.println("Lote ejecutado. Resultados (filas afectadas por sentencia):");
                for (int res : resultados) {
                    System.out.print(res + " ");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    private static void demoSelectConMapeo() {
        String sqlSelect = "SELECT id, nombre, email FROM usuarios WHERE nombre LIKE ?";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sqlSelect)) {
            ps.setString(1, "C%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nombre = rs.getString("nombre");
                    String email = rs.getString("email");
                    usuarios.add(new Usuario(id, nombre, email));
                }

                usuarios.forEach(System.out::println);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    }

    private static void demoInsertPreparedStatement() {
        String sqlInsert = "INSERT INTO usuarios(nombre, email) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {

            PreparedStatement preparedStatement = conn.prepareStatement(sqlInsert);
            preparedStatement.setString(1, "Chelu");
            preparedStatement.setString(2, "chelu@gmail.com");
            int filasAfectadas = preparedStatement.executeUpdate();
            System.out.println("Filas afectadas: " + filasAfectadas);

            PreparedStatement preparedStatement2 = conn.prepareStatement(sqlInsert);
            preparedStatement2.setString(1, "Antono");
            preparedStatement2.setString(2, "antono@gmail.com");
            filasAfectadas = preparedStatement2.executeUpdate();
            System.out.println("Filas afectadas: " + filasAfectadas);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void demoDDL() {
        // SQL para DDL
        String sqlDropUsuarios = "DROP TABLE IF EXISTS usuarios";
        String sqlCreateUsuarios = "CREATE TABLE usuarios (" +
                " id INT AUTO_INCREMENT PRIMARY KEY," +
                " nombre VARCHAR(100) NOT NULL," +
                " email VARCHAR(100) NOT NULL UNIQUE" +
                ")";

        String sqlDropCuentas = "DROP TABLE IF EXISTS cuentas";
        String sqlCreateCuentas = "CREATE TABLE cuentas (" +
                " id VARCHAR(10) PRIMARY KEY," +
                " saldo DECIMAL(10, 2) NOT NULL" +
                ")";

        String sqlInsertCuentas = "INSERT INTO cuentas(id, saldo) VALUES ('A', 1000.00), ('B', 500.00)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlDropUsuarios);
            stmt.execute(sqlCreateUsuarios);
            stmt.execute(sqlDropCuentas);
            stmt.execute(sqlCreateCuentas);
            stmt.execute(sqlInsertCuentas);

        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }
    }
}