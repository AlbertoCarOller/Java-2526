package GestorDDBBJaxb;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class GestorDDBBJaxb {
    // Creamos el Properties, con el cual vamos a poder acceder a las rutas
    private final Properties prop;
    // Creamos el contexto, en este caso siempre va a ser el de la base de datos basándose en la clase Catálogo
    JAXBContext context;
    // Creamos el Path de la base de datos
    String rutaBBDD;
    // Creamos el Path del CSV
    String rutaCSV;
    // Creamos la clase Concesionario como atributo
    private Concesionario concesionario;

    // Creamos el constructor vacío
    public GestorDDBBJaxb() throws GestorBBDDJaxbExcepcion, IOException, JAXBException {
        this.concesionario = new Concesionario();
        // Creamos el contexto, en este el Concesario
        this.context = JAXBContext.newInstance(Concesionario.class);
        // Inicializamos el properties
        prop = new Properties();
        // Cargamos la información del .properties al objeto Properties
        leerProperties();
        // Cargamos la la ruta
        this.rutaBBDD = prop.getProperty("path.bbdd.xml");
        // Comprobamos que la hayamos podido obtener del properties
        if (this.rutaBBDD == null) {
            throw new GestorBBDDJaxbExcepcion("No se ha encontrado la ruta de la bbdd en el properties");
        }
        // Cargamos la ruta del CSV
        this.rutaCSV = prop.getProperty("path.csv");
        // Comprobamos que la hayamos podido obtener del properties
        if (this.rutaCSV == null) {
            throw new GestorBBDDJaxbExcepcion("No se ha encontrado la ruta de la csv en el properties");
        }
        // Creamos la bbdd en caso de que no exista
        if (!new File(this.rutaBBDD).exists()) {
            Files.createFile(Path.of(this.rutaBBDD));
        }
        // Creamos el documento XML con el concesionario vacío
        try(OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(this.rutaBBDD))) {
            // Creamos el marshaller para poder crear el XML
            Marshaller m = context.createMarshaller();
            // Formateamos para que se idente bien al crear el XML
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            // Creamos el XML
            m.marshal(this.concesionario, out);
        }
    }

    /**
     * Esta función va a cargar la información al Properties del fichero
     * .properties y va a buscar el path que corresponda al nombre pasado
     * por parámetros, en caso de que no se encuentre se lanzará excepción
     *
     * @return el path del fichero correspondiente
     * @throws IOException
     */
    private void leerProperties() throws IOException {
        /* Nos apoyamos en el InputStreamReader para leer el .porperties, además este lector está diseñado
         * para leer los ficheros pares clave-valor como es el caso del .properties, incluido los comentarios */
        try (InputStreamReader leer = new InputStreamReader(new FileInputStream("src/main/java/GestorDDBBJaxb/config.properties"))) {
            // Cargamos al objeto Properties el contenido del fichero .properties
            prop.load(leer);
        }
    }

    /**
     * Esta función va a comprobar si existe o no la matrícula pasada por parámetros
     *
     * @param matricula la matrícula a comprobar su existencia
     * @return si existe o no
     * @throws JAXBException
     * @throws GestorBBDDJaxbExcepcion
     * @throws IOException
     */
    private boolean existeMatricula(String matricula) {
        return this.concesionario.getCoches().stream().anyMatch(c -> c.getMatricula().equals(matricula));
    }

    // TODO: por comentar y quizás falte la sobreescritura
    public void agregarCoche(Coche coche) throws GestorBBDDJaxbExcepcion, IOException, JAXBException {
        // Comprobamos si ya existe el coche
        if (existeMatricula(coche.getMatricula())) {
            throw new GestorBBDDJaxbExcepcion("El coche ya existe en la base de datos");
        }
        // Añadimos el coche al concesionario
        concesionario.getCoches().add(coche);

        // Creamos el flujo de escritura
        try(OutputStreamWriter escribir = new OutputStreamWriter(new FileOutputStream(prop.getProperty("path.bbdd.xml")))) {
            Marshaller marshaller = this.context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(this.concesionario, escribir);
        }
    }
}