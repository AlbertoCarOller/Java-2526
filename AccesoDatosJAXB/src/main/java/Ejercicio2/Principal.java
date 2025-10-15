package Ejercicio2;

import java.nio.file.Path;
import java.util.List;

public class Principal {
    public static void main(String[] args) {
        try {
            // Creamos videojuegos y el catálogo
            Videojuego v1 = new Videojuego(1, "Elden Ring", "FromSoftware", 16);
            Videojuego v2 = new Videojuego(2, "The Last of Us Part I", "Naughty Dog", 18);
            Videojuego v3 = new Videojuego(3, "Super Mario Bros. Wonder", "Nintendo", 3);
            Videojuego v4 = new Videojuego(4, "Hades", "Supergiant Games", 12);
            Videojuego v5 = new Videojuego(5, "Baldur's Gate 3", "Larian Studios", 18);
            Catalogo catalogo = new Catalogo("PlayStation", List.of(v1, v2, v3, v4, v5));

            // Creamos el Path de XML del catálogo
            Path path = Path.of("src/main/java/Ejercicio2/Catalogo.xml");

            // Transformamos a XML
            Transformador transformador = new Transformador();
            transformador.transformarAXML(catalogo, path);

            // Destransformar de XML
            Catalogo catalogo1 = transformador.transformarAObjeto(path);
            // Mostramos el catálogo
            System.out.println(catalogo1);

            // Transformamos a Json
            transformador.transformarAJSON(Path.of("src/main/java/Ejercicio2/Catalogo.json"), catalogo);

        } catch (Exception e) {
            throw new RuntimeException(e);

        }
    }
}
