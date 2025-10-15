package Ejercicio2;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.FileReader;
import java.io.IOException;
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
    public Catalogo transformarAObjeto(Path origen) throws IOException, JAXBException {
        try (FileReader reader = new FileReader(origen.toFile())) {
            JAXBContext jaxbContext = JAXBContext.newInstance(Catalogo.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            // Devolvemos el cátalogo
            return (Catalogo) unmarshaller.unmarshal(reader);
        }
    }

    public void transformarAJSON(Path origen, Catalogo catalogo) throws Exception {
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
        try (Jsonb jsonb = JsonbBuilder.create(jacksonConfig); OutputStream o = Files.newOutputStream(origen)) {
            /* Con .toJson(objetoATransformar, flujo de escritura) transforma el objeto a Json, no solo acepta
             flujos binarios de escritura */
            jsonb.toJson(catalogo, o);
        }
    }
}
