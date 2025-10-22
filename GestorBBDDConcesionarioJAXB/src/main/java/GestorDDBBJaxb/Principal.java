package GestorDDBBJaxb;

import jakarta.xml.bind.JAXBException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Principal {
    // NOTA: Cuando se crea una colección mediante .of estas se vuelven inmutables, hay que envolverlas para que sean mutables
    public static void main(String[] args) {
        try {
            // Creamos el gestor de bbdd
            GestorDDBBJaxb gestorDDBBJaxb = new GestorDDBBJaxb();
            gestorDDBBJaxb.agregarCoche(new Coche("345SMTQ", "Mercedes", "B50", new ArrayList<>(List.of("Escoba"))));
            gestorDDBBJaxb.agregarCoche(new Coche("810TYBU", "Ferrari", "X12", new ArrayList<>(List.of("Escoba"))));
            gestorDDBBJaxb.agregarCoche(new Coche("195BJWE", "Lamborgini", "Urus", new ArrayList<>()));
            gestorDDBBJaxb.importarCocheCSV();
            gestorDDBBJaxb.ordenarPorMatricula();
            gestorDDBBJaxb.eliminarCoche("345SMTQ");
            gestorDDBBJaxb.modificarMarca("810TYBU", "Lapolinni");
            gestorDDBBJaxb.modificarExtra("3456JKL", "envenenador", 0);
            gestorDDBBJaxb.modificarModelo("810TYBU", "lili");
            gestorDDBBJaxb.pasarJSON(true, "810TYBU");
        } catch (IOException | GestorBBDDJaxbExcepcion | JAXBException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
