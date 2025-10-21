package GestorDDBBJaxb;

import jakarta.xml.bind.JAXBException;

import java.io.IOException;

public class Principal {
    public static void main(String[] args) {
        try {
            // Creamos el gestor de bbdd
            GestorDDBBJaxb gestorDDBBJaxb = new GestorDDBBJaxb();

        } catch (IOException | GestorBBDDJaxbExcepcion | JAXBException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
