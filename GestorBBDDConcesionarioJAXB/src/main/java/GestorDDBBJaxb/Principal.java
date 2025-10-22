package GestorDDBBJaxb;

import jakarta.xml.bind.JAXBException;

import java.io.IOException;
import java.util.List;

public class Principal {
    public static void main(String[] args) {
        try {
            // Creamos el gestor de bbdd
            GestorDDBBJaxb gestorDDBBJaxb = new GestorDDBBJaxb();
            gestorDDBBJaxb.agregarCoche(new Coche(1, "SMTQ345", "Mercedes", "B50", List.of("Escoba")));
            gestorDDBBJaxb.agregarCoche(new Coche(2, "TYBU810", "Ferrari", "X12", List.of("Rueda", "Martillo")));
            gestorDDBBJaxb.agregarCoche(new Coche(3, "BJWE195", "Lamborgini", "Urus", null));

        } catch (IOException | GestorBBDDJaxbExcepcion | JAXBException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
