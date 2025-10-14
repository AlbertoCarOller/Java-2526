package Ejercicio1XML;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Principal {
    public static void main(String[] args) {
        // Creamos estudiantes
        Estudiante estudiante1 = new Estudiante("2387W", "Alberto", 20);
        Estudiante estudiante2 = new Estudiante("9812N", "Antonio", 37);
        Estudiante estudiante3 = new Estudiante("1859V", "Chelu", 40);

        // Creamos un curso, añadiendo a los estudiantes
        Curso curso1 = new Curso(2134, "Ejercicio1XML.Curso 2DAM", List.of(estudiante1, estudiante2, estudiante3));

        // Creamos un flujo de escritura para escribir el xml en un fichero
        try (OutputStream out = Files.newOutputStream(Path.of("src/main/java/Ejercicio1XML/Curso.xml"))) {
            JAXBContext jaxbContext = JAXBContext.newInstance(Curso.class); /* -> Esta clase crea el contexto, es el
            cerebro traductor que sabe como crear un XML gracias a las anotaciones*/
            // Gracias al JAXBContext podemos crea un objeto de tipo Marshaller, que es el que transforma a XML
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            // Con Marshaller.JAXB_FORMATTED_OUTPUT a true formateamos el XML para que no se forme en una sola línea
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            /* Marshaller transforma a XML gracias a su función .marshal(objetoATransformar, escritura),
             * la razón por la que solo acepta binarios el Marshaller es porque trabaja internamente con
             * binarios, al final el XML es un texto codificado */
            jaxbMarshaller.marshal(curso1, out);

        } catch (IOException | JAXBException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Creamos un flujo de lectura para crear objetos XML a objetos de Java
        try (FileReader leer = new FileReader("src/main/java/Ejercicio1XML/CursoV2.xml")) {
            // Creamos el contexto para poder crear un Unmarshaller, pasándole la clase que tiene las anotaciones
            JAXBContext jaxbContext = JAXBContext.newInstance(Curso.class);
            // Creamos el Unmarshaller que es el que sabe como transforma el XML a objetos Java
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            /* La función .unmarshal() acepta tanto lectores binarios como de texto plano entre otros,
             * ya que el XML en su esencia es un texto plano con etiquetas y JABX lo codifica a UTF-8
             * (<?xml version="1.0" encoding="UTF-8" standalone="yes"?>) */
            Curso curso2 = (Curso) jaxbUnmarshaller.unmarshal(leer); // -> Nos devuelve el objeto, HAY QUE CASTEARLO
            // Mostramos la información del curso
            System.out.println("Nombre: " + curso2.getNombre() + " Id: " + curso2.getId()
                    + "Estudiantes: " + curso2.getEstudiantes());

        } catch (IOException | JAXBException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
