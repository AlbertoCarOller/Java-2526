package Ejercicio2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Transformador {

    /**
     * Esta función transforma el Objeto Java a XML
     *
     * @param catalogo el objeto a transformar
     * @param destino  donde se debe crear el XML
     * @throws IOException
     * @throws JAXBException
     */
    public void transformarAXML(Catalogo catalogo, Path destino) throws IOException, JAXBException {
        try (OutputStream ou = Files.newOutputStream(destino)) {
            // Creamos el contexto
            JAXBContext jaxbContext = JAXBContext.newInstance(Catalogo.class);
            // Creamos la clase que puede transformar
            Marshaller marshaller = jaxbContext.createMarshaller();
            // Indicamos que esté bien formateado
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            // Transformamos
            marshaller.marshal(catalogo, ou);
        }
    }

    /**
     * Esta función transforma un XML a objeto Java
     *
     * @param origen desde donde se debe leer el XML
     * @return el objeto cuando ha sido transformado
     * @throws IOException
     * @throws JAXBException
     */
    public Catalogo transformarDeXMLAObjeto(Path origen) throws IOException, JAXBException {
        try (FileReader reader = new FileReader(origen.toFile())) {
            JAXBContext jaxbContext = JAXBContext.newInstance(Catalogo.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            // Devolvemos el cátalogo
            return (Catalogo) unmarshaller.unmarshal(reader);
        }
    }

    /**
     * Esta función va a transformar un objeto Java a un Json mediante las
     * clases necesarias que hacen el trabajo
     *
     * @param destino  es donde se debe crear el .json
     * @param catalogo el objeto catálogo a transformar
     * @throws Exception
     */
    public void transformarAJSON(Path destino, Catalogo catalogo) throws Exception {
        /* JsonbConfig -> es una clase de configuración antes de la creación del Json, en este
         * caso con withFormatting(true) le decimos que tenga formato, que esté identado,
         * después con .withNullValues(true) -> esta función nos permite indicar que acepte
         * null cuando no haya datos en algunos campos */
        JsonbConfig jacksonConfig = new JsonbConfig().withFormatting(true)
                .withNullValues(true);
        /* Jsonb es el que sabe como transformar un objeto Java a un Json, es el cecrebro traductor,
         * se necesita de un constructor para crear este objeto en este caso hablamos del JsonbBuilder
         * que con .create() crea el objeto Jsonb, además esta función como vemos acepta un JsonbConfig para
         * que sepa con que configuración debe crear el Json */
        try (Jsonb jsonb = JsonbBuilder.create(jacksonConfig); OutputStream o = Files.newOutputStream(destino)) {
            /* Con .toJson(objetoATransformar, flujo de escritura) transforma el objeto a Json, no solo acepta
             flujos binarios de escritura */
            jsonb.toJson(catalogo, o);
        }
    }

    /**
     * Esta función va a transformar el .json a un objeto de la clase Java
     *
     * @param origen el origen, es decir donde se encuentra el .json
     * @return el objeto Java
     * @throws Exception
     */
    public Catalogo transformarDeJSONAObjeto(Path origen) throws Exception {
        try (Jsonb jsonb = JsonbBuilder.create(); InputStream leer = Files.newInputStream(origen)) {
            // .fromJson(flujoDeLectura, laClaseATransformar), acepta no solo flujos binarios
            return jsonb.fromJson(leer, Catalogo.class);
        }
    }

    /**
     * Esta función va a pasar de Json a un objeto de Java y de un objeto de Java
     * a un XML
     *
     * @param origenJSON donde se encuentra el archivo .json
     * @param destinoXML donde se debe de crear el archivo .xml
     * @throws Exception
     */
    public void pasarDeJsonAXML(Path origenJSON, Path destinoXML) throws Exception {
        transformarAXML(transformarDeJSONAObjeto(origenJSON), destinoXML);
    }

    /**
     * Esta función va a pasar de XML a un objeto Java y de objeto a
     * JSON
     *
     * @param destinoXML donde se encuentra el .xml
     * @param origenJSON donde se debe crear el .json
     * @throws Exception
     */
    public void pasarXMLAJson(Path destinoXML, Path origenJSON) throws Exception {
        transformarAJSON(origenJSON, transformarDeXMLAObjeto(destinoXML));
    }

    /**
     * Esta función pasa un POJO a un .json, utilizando la clase
     * estándar ObjectMapper, más utilizada que Jsonb
     *
     * @param catalogo el objeto a transformar
     * @param destino  donde se debe de crear el .json
     * @throws IOException
     */
    public void transformarObjetoAJackson(Catalogo catalogo, Path destino) throws IOException {
        /* ObjectMapper -> es el traductor universal entre un objeto Java, conocido como
         * POJO (Plain Old Java Object) y un archivo JSON */
        ObjectMapper mapper = new ObjectMapper();
        // .enable() es una función de configuración, en este caso configuramos que tenga identación
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        // Creamos el flujo de escritura
        try (OutputStream o = Files.newOutputStream(destino)) {
            // Con la función .writeValue(escritura, objeto) creamos el JSON, acepta no solo flujos binarios
            mapper.writeValue(o, catalogo);
        }
    }

    /**
     * Esta función transforma un archivo .json a
     * un objeto Java mediante la clase estándar ObjectMapper
     *
     * @param origen donde se encuentra el JSON
     * @return el objeto ya transformado
     * @throws IOException
     */
    public Catalogo transformarJacksonAObjeto(Path origen) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream leer = Files.newInputStream(origen)) {
            // .readValue(leer, Objeto) -> transforma el JSON a un objeto de Java, acepta no solo flujos binarios
            return mapper.readValue(leer, Catalogo.class);
        }
    }
}