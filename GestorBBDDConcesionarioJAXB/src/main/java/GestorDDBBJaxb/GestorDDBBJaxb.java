package GestorDDBBJaxb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class GestorDDBBJaxb {
    // Creamos el Properties, con el cual vamos a poder acceder a las rutas
    private final Properties prop;
    // Creamos el contexto, en este caso siempre va a ser el de la base de datos basándose en la clase Catálogo
    private JAXBContext context;
    // Creamos el Path de la base de datos
    private String rutaBBDD;
    // Creamos el Path del CSV
    private String rutaCSV;
    // Creamos la clase Concesionario como atributo
    private Concesionario concesionario;
    // Creamos el ObjectMapper para mapear a JSON y desmapera de JSON a Objet
    private ObjectMapper mapper;
    // La ruta de exportación JSON
    private String rutaExportacionJSON;

    // Creamos el constructor
    public GestorDDBBJaxb() throws GestorBBDDJaxbExcepcion, IOException, JAXBException {
        // Declaramos el ObjectMapper
        mapper = new ObjectMapper();
        // Creamos el contexto, en este el Concesionario
        this.context = JAXBContext.newInstance(Concesionario.class);
        // Inicializamos el properties
        prop = new Properties();
        // Cargamos la información del .properties al objeto Properties
        leerProperties();
        // Cargamos la la ruta de la base de datos
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
        // Cargamos la ruta del JSON
        this.rutaExportacionJSON = prop.getProperty("path.json");
        // Comprobamos que la hayamos podido obtener del properties
        if (this.rutaExportacionJSON == null) {
            throw new GestorBBDDJaxbExcepcion("No se ha encontrado la ruta de exportación a JSON en el properties");
        }
        // Creamos la bbdd en caso de que no exista
        if (!new File(this.rutaBBDD).exists()) {
            Files.createFile(Path.of(this.rutaBBDD));
            // Creamos el concesionario vacío
            this.concesionario = new Concesionario();
            // Creamos el documento XML con el concesionario vacío
            importarParaXML();

            // En caso de que ya exista un XML, se carga en el objeto
        } else {
            this.concesionario = deseliarizarXML();
            // Actualizamos el id
            actualizarID();
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
     * Esta función va a deserializar un XML a un Objeto, en este caso a un Concesionario
     *
     * @return devuelve el concesionario
     * @throws IOException
     * @throws JAXBException
     */
    private Concesionario deseliarizarXML() throws IOException, JAXBException {
        try (InputStreamReader leer = new InputStreamReader(new FileInputStream(prop.getProperty("path.bbdd.xml")))) {
            Unmarshaller unmarshaller = this.context.createUnmarshaller();
            return (Concesionario) unmarshaller.unmarshal(leer);
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
    public void agregarCoche(Coche coche) throws GestorBBDDJaxbExcepcion, IOException, JAXBException {
        // Añadimos el coche al objeto Concesionario
        if (!concesionario.getCoches().add(coche)) {
            throw new GestorBBDDJaxbExcepcion("El coche ya existe");
        }
        // Volcamos los cambios
        importarParaXML();
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
                                .map(String::trim).collect(Collectors.toCollection(ArrayList::new)))); /* -> Hacemos un flujo
                                 para hacer trim por cada extra */
                        // Confirmamos que el coche tenga todos los campos obligatorios para poder formarlo
                        contadorCochesAgregados++;
                    } else if (coche.length == 3) {
                        this.concesionario.getCoches().add(new Coche(coche[0].trim(), coche[1].trim(), coche[2].trim(), new ArrayList<>()));
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
                .equals(matricula)).findAny()).orElseThrow(() -> new GestorBBDDJaxbExcepcion("No se ha encontado el coche"));
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

    /**
     * Esta función va a modificar la marca del coche con la matrícula pasada
     *
     * @param matricula la matrícula del vehículo a modificar
     * @param marca     la nueva marca
     * @throws GestorBBDDJaxbExcepcion
     */
    public void modificarMarca(String matricula, String marca) throws GestorBBDDJaxbExcepcion, JAXBException, IOException {
        obtenerCoche(matricula).setMarca(marca); // -> Modificamos la marca
        // Volcamos los datos
        importarParaXML();
    }

    /**
     * Esta función va a modificar el modelo del coche con la matrícula pasada
     *
     * @param matricula la matrícula del vehículo a modificar
     * @param modelo    el nuevo modelo
     * @throws GestorBBDDJaxbExcepcion
     */
    public void modificarModelo(String matricula, String modelo) throws GestorBBDDJaxbExcepcion, JAXBException, IOException {
        obtenerCoche(matricula).setModelo(modelo); // -> Modificamos el modelo
        // Volcamos los datos
        importarParaXML();
    }

    /**
     * Esta función va a modificar el extra con el índice indicado
     * por el usuario
     *
     * @param matricula   la matrícula del vehículo a buscar
     * @param extra       el extra nuevo que va a introducir
     * @param indiceExtra el índice donde se encuentra el extra a modificar
     * @throws GestorBBDDJaxbExcepcion
     */
    public void modificarExtra(String matricula, String extra, int indiceExtra) throws GestorBBDDJaxbExcepcion, JAXBException, IOException {
        obtenerCoche(matricula).getEquipamiento().set(indiceExtra, extra); // -> Modificamos un extra concreto
        // Volcamos los datos
        importarParaXML();
    }

    /**
     * Esta función va a exportar a un archivo JSON un coche o el concesionario
     * completo dependiendo de lo que pida el usuario
     *
     * @param soloCoche para indicar si queremos exportar solo un coche o no
     * @param matricula en caso de que quiera exportar un coche, se le indica la matrícula del coche que quieras
     * @throws IOException
     * @throws GestorBBDDJaxbExcepcion
     */
    public void pasarJSON(boolean soloCoche, String matricula) throws IOException, GestorBBDDJaxbExcepcion {
        try (OutputStreamWriter escribir = new OutputStreamWriter(new FileOutputStream(prop.getProperty("path.json")))) {
            // Configuramos el JSON para que su identación sea correcta
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            // Si solo quiere pasar un coche
            if (soloCoche) {
                mapper.writeValue(escribir, obtenerCoche(matricula));

                // En caso de que quiera pasar toda la base de datos, es decir, el Concesionario
            } else {
                mapper.writeValue(escribir, this.concesionario);
            }
        }
    }

    /**
     * Esta función va a recalcular el id que va a empezar a tener
     * los próximos cohes que se vayan añadiendo a la base de datos
     * teniendo en cuenta que al haber una base de datos o quere importar
     * el JSON el id debe continuar con el número posterior del id más
     * grande
     */
    private void actualizarID() throws GestorBBDDJaxbExcepcion {
        // Recalculamos el id de los coches porque si no, empezará desde el principio y si cargamos coches el id no puede repetirse
        Coche.idAuxiliar = (this.concesionario.getCoches().stream().max(Comparator.comparingInt((c) -> c.getId())))
                .orElseThrow(() -> new GestorBBDDJaxbExcepcion("No se han encontrados coches")).getId();
    }

    /**
     * Esta función va a importar un JSON, hay dos opciones, puede ser tanto un nuevo
     * concesionario que sustituirá la bbdd actual o bien importa un JSON que contiene
     * un coche
     *
     * @param todo controla si se quiere importar un concesionario o un coche
     * @throws IOException
     * @throws JAXBException
     * @throws GestorBBDDJaxbExcepcion
     */
    public void importarJSON(boolean todo) throws IOException, JAXBException, GestorBBDDJaxbExcepcion {
        // En caso de querer importar el concesionario completo entra
        if (todo) {
            // Abrimos un flujo de lectura del concesionario nuevo
            try (InputStreamReader leer = new InputStreamReader(new FileInputStream(prop.getProperty("path.jsonIConcesionario")))) {
                // Sobreescribimos el objeto concesionario
                this.concesionario = this.mapper.readValue(leer, Concesionario.class);
                // Lo volcamos al XML
                importarParaXML();
                // Actualizamos el id
                actualizarID();
            }
            // En caso de querer solo inportar un coche y añadirlo
        } else {
            try (InputStreamReader leer = new InputStreamReader(new FileInputStream(prop.getProperty("path.jsonICoche")))) {
                // Metemos el coche en el concesionario
                agregarCoche(this.mapper.readValue(leer, Coche.class));
                // Lo volcamos al XML
                importarParaXML();
            }
        }
    }

    /**
     * Esta función va a crear un archivo de texto donde se va a guardar información
     * general sobre la base de datos
     *
     * @throws IOException
     */
    public void realizarResumen() throws IOException {
        // Flujo de escritura
        try (BufferedWriter escribir = new BufferedWriter(new FileWriter(prop.getProperty("path.resumen")))) {
            escribir.write("Número total de coches: " + this.concesionario.getCoches().size() + "\n\n" +
                    "Coches agrupados por marca: " + (!cochesPorMarca().isEmpty() ? cochesPorMarca() : "No hay datos") + "\n\n" +
                    "Equipamiento que más se repite: " + equipamientoMasRepetido());
        }
    }

    /**
     * Esta función va a transformar un la lista de coches
     * en un mapa agrupado por matrícula
     *
     * @return un mapa de coches por marca
     */
    private String cochesPorMarca() {
        // groupingBy() -> Agrupa por un key pasado, el value será una lista de los objetos que tengan esa key
        return this.concesionario.getCoches().stream().collect(Collectors.groupingBy(((c) -> c.getMarca())))
                .entrySet().stream().map((e) -> e.getKey() + ": " + e.getValue()).collect(Collectors
                        .joining("\n"));
    }

    /**
     * Esta función va a buscar la herramienta más utilizada de todas, lo hace agrupando todas
     * contando así con Collectors.counting() cuantas veces aparecen, en caso de que no haya ninguna
     * devolvemos un mensaje diciendo que no hay datos, SE TIENE EN CUENTA QUE HAYA VARIOS MAX
     *
     * @return el equipamiento o 'No hay datos'
     */
    private List<String> equipamientoMasRepetido() {
        /* Obtenemos un mapa de todas las herramientas y el número de veces que aparece, Collectors.counting()
         cuenta las veces que aparece el key, se tiene en cuenta que haya varios max */
        return this.concesionario.getCoches().stream().flatMap((c) -> c.getEquipamiento().stream())
                .collect(Collectors.groupingBy(s -> s, Collectors.counting())).
                entrySet().stream().filter(e -> e.getValue().equals(this.concesionario.getCoches()
                        .stream().flatMap((c) -> c.getEquipamiento().stream())
                        .collect(Collectors.groupingBy(s -> s, Collectors.counting())).
                        entrySet().stream().max(Map.Entry.comparingByValue())
                        // En caso de que no haya datos, se devolverá 'no hay datos'
                        .orElseGet(() -> Map.entry("No hay datos", 0L)).getValue()))
                .map(Map.Entry::getKey).toList();
    }
}