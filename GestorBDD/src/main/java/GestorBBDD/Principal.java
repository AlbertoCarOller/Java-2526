package GestorBBDD;

import java.io.IOException;

public class Principal {
    public static void main(String[] args) {
        try {
            // TODO: hacer menú
            GestorBBDD gestorBBDD = new GestorBBDD("C:\\Users\\Alberto.DESKTOP-O1GC77M\\Desktop\\Java\\" +
                    "Java-2526\\GestorBDD\\src\\main\\java\\GestorBBDD\\base_de_datos.dat",
                    "C:\\Users\\Alberto.DESKTOP-O1GC77M\\Desktop\\Java\\Java-2526\\GestorBDD\\" +
                            "src\\main\\java\\GestorBBDD\\BBDD Coches.csv");
            gestorBBDD.insertarRegistro("8986TRE", "SEAT", "LEON", 0);
            gestorBBDD.insertarRegistro("8917421", "RENAULT", "C9", 1);
            gestorBBDD.insertarRegistro("111111R", "FERRARI", "CHELU", 2);
            gestorBBDD.insertarRegistro("007007V", "LAMBORGUINI", "AVENTADOR", 3);

            gestorBBDD.borrarRegistro("007007V");
            gestorBBDD.borrarRegistro("8917421");

            gestorBBDD.modificarRegistro(0, "MERCEDES", "B45");

            gestorBBDD.ordenarPorMatricula();

            gestorBBDD.cargarCSV();

        } catch (IOException | GestorBBDDException | RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
}
