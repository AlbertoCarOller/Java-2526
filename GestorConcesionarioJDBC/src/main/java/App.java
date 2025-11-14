import PersonalExceptions.ConcesionarioExcepcion;
import service.ConcensionarioService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class App {
    public static void main(String[] args) {
        try {
            ConcensionarioService concensionarioService = new ConcensionarioService();
            concensionarioService.iniciarDataBase(true);
//            concensionarioService.insertarCoche("5465XMR", "Ferrari",
//                    "D34", new ArrayList<>(), 12000, true);
            concensionarioService.listarCoches(true, false).forEach(System.out::println);
            /*concensionarioService.insertarPropietario(true, "77590E", "Carles Xavier",
                    "Anglade Muñóz", "");*/
            /*concensionarioService.modificarCoche("3456JKL", "", "Toledo", new ArrayList<>(),
                    -1, true);*/
            //concensionarioService.modificarCoche("7890MNP", "", "206", new ArrayList<>(), -1, false);
            //concensionarioService.borrarCoche("5465XMR", true);
            //concensionarioService.iniciarDataBase(false);
        } catch (IOException | SQLException | ConcesionarioExcepcion e) {
            System.out.println("Error" + e.getMessage());
        }
    }
}
