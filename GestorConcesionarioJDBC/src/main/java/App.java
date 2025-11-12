import service.ConcensionarioService;

import java.io.IOException;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) {
        try {
            ConcensionarioService concensionarioService = new ConcensionarioService();
            concensionarioService.comprobarConexionMySQL();
            concensionarioService.crearTablasMySQL();
        } catch (IOException | SQLException e) {
            System.out.println("Error" + e.getMessage());
        }
    }
}
