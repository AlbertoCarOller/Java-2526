package GestorDDBBJaxb;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.stream.Collectors;

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

    // Creamos el constructor
    public GestorDDBBJaxb() throws GestorBBDDJaxbExcepcion, IOException, JAXBException {
        this.concesionario = new Concesionario();
        // Creamos el contexto, en este el Concesionario
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
        try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(this.rutaBBDD))) {
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
     * Esta función va a añadir un coche al objeto Concesionario y lo
     * pasamos al XML, es decir la bbdd
     *
     * @param coche el objeto coche el cual queremos introducir
     * @throws GestorBBDDJaxbExcepcion
     * @throws IOException
     * @throws JAXBException
     */
    public boolean agregarCoche(Coche coche) throws GestorBBDDJaxbExcepcion, IOException, JAXBException {
        // Para posteriormente decir si se ha añadido o no
        boolean inserccionado = false;
        // Añadimos el coche al objeto Concesionario
        inserccionado = concesionario.getCoches().add(coche);
        // Volcamos los cambios
        importarParaXML();
        return inserccionado;
    }

    /**
     * Esta función va a guardar los coches del CSV eal conjunto de coches
     * del concesionario, evitando repetidos, gracias al Set, después volcamos
     * el objeto a la base de datos XML
     *
     * @throws IOException
     */
    public int importarCocheCSV() throws IOException, JAXBException {
        int contadorCochesAgregados = 0;
        // Creamos el flujo de lectura para el CSV
        try (BufferedReader leer = new BufferedReader(new FileReader(prop.getProperty("path.csv")))) {
            // La línea que ha leído
            String linea;
            // Contador de líneas, para que se salte la primera línea
            int contador = 0;
            // Mientras haya líneas
            while ((linea = leer.readLine()) != null) {
                // No se lee la primera línea
                if (contador != 0) {
                    // Separamos los campos del coche
                    String[] coche = linea.split(";");
                    // Comprobamos si el coche tiene equipamientos
                    if (coche.length == 4) {
                        // Obtenemos los extras de cada coche
                        String[] extras = coche[3].split("\\|");
                        // Añadimos un coche con equipamientos
                        this.concesionario.getCoches().add(new Coche(coche[0].trim(),
                                coche[1].trim(), coche[2].trim(), Arrays.stream(extras)
                                .map(String::trim).toList())); // -> Hacemos un flujo para hacer trim por cada extra
                        // Confirmamos que el coche tenga todos los campos obligatorios para poder formalo
                        contadorCochesAgregados++;
                    } else if (coche.length == 3) {
                        this.concesionario.getCoches().add(new Coche(coche[0].trim(), coche[1].trim(), coche[2].trim(), null));
                        contadorCochesAgregados++;
                    }
                }
                contador++;
            }
        }
        // Volcamos los cambios
        importarParaXML();
        return contadorCochesAgregados;
    }

    /**
     * Esta es una función auxiliar que va a importar el objeto Concesionario
     * a un archivo XML
     *
     * @throws JAXBException
     * @throws IOException
     */
    private void importarParaXML() throws JAXBException, IOException {
        try (OutputStreamWriter escribir = new OutputStreamWriter(new FileOutputStream(prop.getProperty("path.bbdd.xml")))) {
            // Creamos el Marshaller gracias al contexto
            Marshaller marshaller = this.context.createMarshaller();
            // Formateamos
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            // Transformamos a el objeto a XML
            marshaller.marshal(this.concesionario, escribir);
        }
    }

    /**
     * Esta función va a ordenar la lista de coches del concesionario,
     * en este caso se va a ordenar por el campo matrícula
     *
     * @throws JAXBException
     * @throws IOException
     */
    public void ordenarPorMatricula() throws JAXBException, IOException {
        this.concesionario.setCoches(this.concesionario.getCoches().stream()
                .sorted((m1, m2) -> m2.getMatricula().compareTo(m1.getMatricula()))
                // Gracias al LinkedHashSet sí se puede mantener el orden
                .collect(Collectors.toCollection(LinkedHashSet::new)));
        // Volcamos la información
        importarParaXML();
    }

    /**
     * Esta función buscar un coche por la matrícula, en caso de que no exista se lanzará excepción
     *
     * @param matricula la matrícula a introducir para buscar el coche
     * @return devuelve el objeto Coche
     * @throws GestorBBDDJaxbExcepcion
     */
    private Coche obtenerCoche(String matricula) throws GestorBBDDJaxbExcepcion {
        return (this.concesionario.getCoches().stream().filter(c -> c.getMatricula()
                .equals(matricula)).findAny()).orElseThrow(() -> new GestorBBDDJaxbExcepcion("No se ha encontado al coche"));
    }

    /**
     * Esta función elimina el coche de la lista del concesionario
     *
     * @param matricula la matrícula
     * @throws GestorBBDDJaxbExcepcion
     */
    public void eliminarCoche(String matricula) throws GestorBBDDJaxbExcepcion, JAXBException, IOException {
        // Eliminamos el coche
        this.concesionario.getCoches().remove(obtenerCoche(matricula));
        // Volcamos los datos
        importarParaXML();
    }
}