package org.example;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Principal {
    public static void main(String[] args) {
        try {
            Map<String,Integer> esquema = new LinkedHashMap<>();
            esquema.put("DNI", 9);
            esquema.put("NOMBRE", 32);
            esquema.put("CP", 5);

            GestorBBDDP gestorDB = new GestorBBDDP("mi_base_de_datos.dat", esquema, "DNI");
            System.out.println("Estado inicial. Registros: " + gestorDB.getTotalRegistros());

            // --- Inserciones ---
            HashMap<String, String> nuevoRegistro = new HashMap<>();
            nuevoRegistro.put("DNI", "12345678Z"); nuevoRegistro.put("NOMBRE", "ANA ARCOS"); nuevoRegistro.put("CP", "29730");
            gestorDB.insertar(nuevoRegistro);
            nuevoRegistro.clear();
            nuevoRegistro.put("DNI", "56789012B"); nuevoRegistro.put("NOMBRE", "LUIS SAMPER"); nuevoRegistro.put("CP", "29730");
            gestorDB.insertar(nuevoRegistro);
            nuevoRegistro.clear();
            nuevoRegistro.put("DNI", "89012345E"); nuevoRegistro.put("NOMBRE", "CARLA ROJAS"); nuevoRegistro.put("CP", "13700");
            gestorDB.insertar(nuevoRegistro);

            System.out.println("\nTras insertar. Registros: " + gestorDB.getTotalRegistros());

            // --- Recuperar un registro ---
            System.out.println("\nRecuperando DNI 56789012B...");
            System.out.println(gestorDB.recuperar("56789012B"));

            // --- Borrado Lógico ---
            System.out.println("\nBorrando DNI 56789012B...");
            gestorDB.borrar("56789012B");
            System.out.println("Registros marcados para borrado: " + gestorDB.getTotalRegistrosMarcadosParaBorrado());

            // --- Compactación ---
            System.out.println("\nCompactando fichero...");
            int suprimidos = gestorDB.compactar();
            System.out.println("Registros suprimidos: " + suprimidos);
            System.out.println("Estado final. Registros: " + gestorDB.getTotalRegistros() + ". Marcados borrado: " + gestorDB.getTotalRegistrosMarcadosParaBorrado());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}