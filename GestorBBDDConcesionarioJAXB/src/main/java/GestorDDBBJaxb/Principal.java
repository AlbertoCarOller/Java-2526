package GestorDDBBJaxb;

import jakarta.xml.bind.JAXBException;

import java.io.IOException;
import java.util.List;

public class Principal {
    public static void main(String[] args) {
        try {
            // Creamos el gestor de bbdd
            GestorDDBBJaxb gestorDDBBJaxb = new GestorDDBBJaxb();
            gestorDDBBJaxb.agregarCoche(new Coche( "345SMTQ", "Mercedes", "B50", List.of("Escoba")));
            gestorDDBBJaxb.agregarCoche(new Coche("810TYBU", "Ferrari", "X12", List.of("Rueda", "Martillo")));
            gestorDDBBJaxb.agregarCoche(new Coche("195BJWE", "Lamborgini", "Urus", null));
            gestorDDBBJaxb.importarCocheCSV();
            gestorDDBBJaxb.ordenarPorMatricula();
            gestorDDBBJaxb.eliminarCoche("345SMTQ");

        } catch (IOException | GestorBBDDJaxbExcepcion | JAXBException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
