package service;

import PersonalExceptions.ConcesionarioExcepcion;
import model.Coche;
import util.ConfigLoader;
import util.ConnectionController;
import util.SQLSentences;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class ConcensionarioService {
    private final ConnectionController connCont;
    private final Properties prop;

    public ConcensionarioService() throws IOException {
        connCont = new ConnectionController(); // -> Controlador de las conexiones con las bases de datos
        prop = new Properties(); // -> Properties que vamos a utilizar para cargar los datos del .properties
        ConfigLoader.cargarProperties(prop); // -> Cargamos los datos en el properties
    }

    /**
     * Se comprueba que la conexión se ha realizado correctamente
     *
     * @return true si la conexión funciona
     * @throws SQLException en caso de que la conexión no funcione, por lo que no devolverá nada
     */
    /*public Boolean comprobarConexionVacia() throws SQLException {
        try (Connection connection = connCont.getConnectionVacia()) {
        }
        // Si no ha saltado excepción devolverá true
        return true;
    }*/

    /**
     * Se comprueba que la conexión se ha realizado correctamente con MySQL a la base de datos
     *
     * @throws SQLException en caso de que la conexión no funcione, por lo que no devolverá nada
     */
    private void comprobarConexionMySQL() throws SQLException {
        // Se crea el esquema para comprobar la conexión
        crearEsquema();
        try (Connection connection = connCont.getConnectionMySQL()) {
        }
    }

    /**
     * Se comprueba que la conexión se ha realizado correctamente con SQLite
     *
     * @throws SQLException en caso de que la conexión no funcione, por lo que no devolverá nada
     */
    private void comprobarConexionSQLite() throws SQLException {
        try (Connection connection = connCont.getConnectionSQLite()) {
        }
    }

    /**
     * Esta función va a crear el esquema de la base de datos,
     * en caso de que ya exista la base de datos, no hace nada
     *
     * @throws SQLException lanza la excepción en caso de error
     */
    private void crearEsquema() throws SQLException {
        try (Connection connection = connCont.getConnectionVacia()) {
            // Se crea la base de datos
            Statement statement = connection.createStatement();
            statement.execute(SQLSentences.SQL_CREATE_SCHEME_MYSQL);
        }
    }

    /**
     * Esta función va a crear las 3 tablas que conforman
     * la estructura de la base de datos
     *
     * @throws SQLException lanza la excepción en caso de error
     */
    private void crearTablasMySQL() throws SQLException {
        // Creamos la conexión nula
        Connection connection = null;
        try {
            // Inicializamos la conexión
            connection = connCont.getConnectionMySQL();
            try (Statement statement = connection.createStatement()) {
                // Creamos una transacción de todo o nada
                connection.setAutoCommit(false);
                // Creamos las tablas
                statement.execute(SQLSentences.SQL_CREATE_TABLE_PROPIETARIOS_MYSQL);
                statement.execute(SQLSentences.SQL_CREATE_TABLE_COCHES_MYSQL);
                statement.execute(SQLSentences.SQL_CREATE_TABLE_TRASPASOS_MYSQL);
                // Hacemos commit si no ha saltado excepción
                connection.commit();
            }
        } catch (SQLException e) {
            // En caso de que no sea nula la conexión y haya excepción, se hace rollback
            if (connection != null) {
                connection.rollback();
                throw new SQLException(e.getMessage()); // -> Lanzar excepción con el mensaje de error
            }
        } finally { // -> Garantiza que se cierre la conexión y se ponga el auto-commit a true, pase lo que pase
            // En caso de que no sea nula la conexión
            if (connection != null) {
                // Se devuelve el auto-commit a true
                connection.setAutoCommit(true);
                // Se cierra la conexión
                connection.close();
            }
        }
    }

    /**
     * Esta función va a crear las tablas necesarias en SQLite
     *
     * @throws SQLException lanza la excepción en caso de error
     */
    private void crearTablasSQLite() throws SQLException {
        // Obtenemos la conexión en null
        Connection connection = null;
        // Obtenemos la conexión con SQLite
        try {
            connection = connCont.getConnectionSQLite();
            try (Statement statement = connection.createStatement()) {
                connection.setAutoCommit(false);
                // Creamos las tablas
                statement.execute(SQLSentences.SQL_CREATE_TABLE_PROPIETARIOS_MYSQLITE);
                statement.execute(SQLSentences.SQL_CREATE_TABLE_COCHES_MYSQLITE);
                statement.execute(SQLSentences.SQL_CREATE_TABLE_TRASPASOS_MYSQLITE);
                // Hacemos el commit
                connection.commit();
            }
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
                throw new SQLException(e.getMessage());
            }
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }


    /**
     * Insertamos los datos obtenidos del CSV directamente en la base de datos con
     * sentencias SQL, las sentencias en este caso para los insert son iguales
     * tanto en MySQL como en SQLite
     *
     * @param coches la lista de coches a insertar
     * @param mysql  si la conexión va a ser con MySQL o no
     * @throws SQLException lanza la excepción en caso de error
     */
    private void insertarDatosCSV(List<Coche> coches, Boolean mysql) throws SQLException, ConcesionarioExcepcion {
        // Creamos una conexión
        Connection connection = null;
        try {
            // Si es MySQL obtenemos la conexión con la base de datos del servidor y si no pues con SQLite
            if (mysql) connection = connCont.getConnectionMySQL();
            else connection = connCont.getConnectionSQLite();
            // Creamos el PreparedStatement con la sentencia SQL para insertar
            try (PreparedStatement preparedStatement = connection.prepareStatement(SQLSentences.SQL_INSERT_COCHE_SQL)) {
                // Ponemos el auto-commit a false
                connection.setAutoCommit(false);
                for (Coche coche : coches) {
                    /* Comprobamos cada campo del coche para saltar excepción en caso de que un campo que debe ser
                     obligatorio no sea correcto, esté en blanco o el precio en -1, que eso querrá decir que no
                      tenía precio, por lo que se deberá de saltar excepción*/
                    String matricula = coche.getMatricula();
                    String marca = coche.getMarca();
                    String modelo = coche.getModelo();
                    double precio = coche.getPrecio();
                    if (matricula.isBlank() || marca.isBlank() || modelo.isBlank() || precio == -1.0) {
                        throw new ConcesionarioExcepcion("Algún campo de algún coche es inválido");
                    }
                    preparedStatement.setString(1, coche.getMatricula());
                    preparedStatement.setString(2, coche.getMarca());
                    preparedStatement.setString(3, coche.getModelo());
                    // Si no hay extras, se inserta NULL (el campo es nullable, así que no hay problema)
                    if (coche.getExtras().isEmpty()) preparedStatement.setNull(4, java.sql.Types.NULL);
                        // En caso de que haya extras, se crea un String formateado con los extras de la lista de extras
                    else preparedStatement.setString(4, String.join(", ", coche.getExtras()));
                    preparedStatement.setDouble(5, coche.getPrecio());
                    // Añadimos cada statement al batch
                    preparedStatement.addBatch();
                }
                // Una vez añadido todos los statement, los ejecutamos todos a la vez
                preparedStatement.executeBatch();
                // Confirmamos, hacemos un commit
                connection.commit();
            }
            /* Aquí no lanzamos la excepción porque en este caso no queremos informar de posibles
             * errores, la excepción que podría atraparse sería que cuando se crea por primera vez
             * la base de datos con los datos del CSV, si volvemos a pulsar esta opción, lo que es
             * la creación de las tablas y la base de datos no dará SQLException porque se controlan
             * con las sentencias SQL, pero se intentará hacer los insert de los datos del CSV, esto
             * lanzará excepción, aquí la atrapamos y la silenciamos */
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }

    /**
     * Esta función va a devolver una lista de coches, va a crear objetos Coche
     * a partir cada línea del CSV
     *
     * @return una lista de coches
     * @throws IOException lanza la excepción en caso de error
     */
    private List<Coche> importarCochesCSV() throws IOException {
        // Creamos la lista a devolver
        List<Coche> coches = new ArrayList<>();
        // Obtenemos todas las líneas del fichero
        List<String> lineas = Files.readAllLines(Path.of(prop.getProperty("pathCSV")));
        // Quitamos la primera hora
        lineas.removeFirst();
        lineas.forEach(line -> {
            // Lista de los campos, guardamos el campo vacío en caso de que haya para comprobar qué campo es
            List<String> campos = Arrays.stream(line.split(";")).toList();
            // Se comprueba que tenga 5 campos
            if (campos.size() == 5) {
                // Agregamos el coche
                coches.add(new Coche(campos.getFirst().trim(), campos.get(1).trim(), campos.get(2).trim(),
                        campos.get(3).isBlank() ? new ArrayList<>() :
                                Arrays.stream(campos.get(3).split("\\|")).map(String::trim)
                                        .collect(Collectors.toCollection(ArrayList::new)),
                        campos.get(4).isBlank() ? -1.0 : Double.parseDouble(campos.get(4)), 0));
            }
        });
        return coches; // -> Devolvemos la lista con los coches ya formados
    }

    /**
     * Esta función recoge todas las acciones que debe hacer la primera opción del menú
     * dependiendo del tipo de conexión que se quiera se hará de una forma u otra
     *
     * @param mysql si la conexión es mysql o no
     * @throws SQLException lanza la excepción en caso de error
     * @throws IOException  lanza la excepción en caso de error
     */
    public void iniciarDataBase(Boolean mysql) throws SQLException, IOException, ConcesionarioExcepcion {
        // Si nos queremos conectar con MySQL
        if (mysql) {
            comprobarConexionMySQL();
            crearTablasMySQL(); // -> Creamos las tablas MySQL
            insertarDatosCSV(importarCochesCSV(), true); // -> Insertamos los datos del CSV
            // Nos conectamos con SQLite
        } else {
            comprobarConexionSQLite();
            crearTablasSQLite(); // -> Creamos las tablas SQLite
            insertarDatosCSV(importarCochesCSV(), false); // -> Insertamos los datos del CSV
        }
        // Se insertan propietarios de ejemplo al iniciar la base de datos
        insertarPropietarioEjemplo(mysql);
    }

    /**
     * Esta función va a insertar en la base de datos un solo coche,
     * se comprueban los campos propensos a estar vacíos para evitar
     * que haya una mala insercción
     *
     * @param matricula la matrícula a insertar
     * @param marca     la marca a insertar
     * @param modelo    el modelo a insertar
     * @param extras    los extras a insertar
     * @param precio    el precio del coche
     * @param mysql     si se hace en la conexión MySQL o SQLite
     * @throws ConcesionarioExcepcion lanza la excepción en caso de error
     * @throws SQLException           lanza la excepción en caso de error
     */
    public void insertarCoche(String matricula, String marca, String modelo, List<String> extras, double precio, boolean mysql)
            throws ConcesionarioExcepcion, SQLException {
        // Comprobamos si los campos son correctos
        if (marca.isEmpty() || modelo.isEmpty() || matricula.isEmpty() || precio < 0) {
            throw new ConcesionarioExcepcion("Algún campo es inválido");
        }
        try (Connection connection = elegirConexion(mysql)) {
            // Creamos el PreparedStatement
            try (PreparedStatement preparedStatement = connection.prepareStatement(SQLSentences.SQL_INSERT_COCHE_SQL)) {
                preparedStatement.setString(1, matricula);
                preparedStatement.setString(2, marca);
                preparedStatement.setString(3, modelo);
                if (extras.isEmpty()) preparedStatement.setNull(4, java.sql.Types.NULL);
                else preparedStatement.setString(4, String.join(", ", extras));
                preparedStatement.setDouble(5, precio);
                // Ejecutamos la insercción
                preparedStatement.executeUpdate();
            }
        }
    }

    /**
     * Esta función va a devolver una lista de coches, dependiendo de lo que quiera el usuario se elegirá
     * la conexión y se elegirá si quiere que se muestre los coches con o sin
     * propietarios
     *
     * @param mysql        la conexión
     * @param propietarios con o sin propietarios
     * @return una lista de coches
     * @throws SQLException lanza la excepción en caso de error
     */
    public List<Coche> listarCoches(boolean mysql, boolean propietarios) throws SQLException {
        // La lista de coches a devolver como Strings para no transformarlos a objetos
        List<Coche> coches;
        try (Connection connection = elegirConexion(mysql)) {
            // Preparamos el Statement
            try (Statement statement = connection.createStatement()) {
                String sqlDecidida;
                // Dependiendo de si queremos mostrar con o sin propietarios se seleccionará un setencia u otra
                if (propietarios) sqlDecidida = SQLSentences.SQL_COCHES_CON_PROPIETARIO_JOIN;
                else sqlDecidida = SQLSentences.SQL_COCHES_SIN_PROPIETARIO;
                // El ResultSet se cierra solo cuando su Statement se cierra
                ResultSet rs = statement.executeQuery(sqlDecidida);
                // Creamos la lista
                coches = obtenerListaCoches(rs);
            }
        }
        return coches;
    }

    /**
     * Esta función va a añadir los resultados de un ResulSet en una lista de String
     *
     * @param rs el ResultSet
     * @return una lista con los coches formateados como Strings
     * @throws SQLException lanza la excepción en caso de error
     */
    private List<Coche> obtenerListaCoches(ResultSet rs) throws SQLException {
        // Lista vacía
        List<Coche> coches = new ArrayList<>();
        // Guardamos los resultados en caso de que hayan
        while (rs.next()) {
            coches.add(new Coche(rs.getString(1),
                    rs.getString(2),
                    rs.getString(3),
                    Arrays.stream((rs.getString(4) == null ? "Sin extras" : rs.getString(4))
                            .split(",")).map(String::trim).collect(Collectors.toCollection(ArrayList::new)),
                    rs.getDouble(5),
                    rs.getInt(6)));
        }
        return coches;
    }

    /**
     * Esta función va a insertar propietarios de ejemplo en la base de datos,
     * al inicializarla
     *
     * @param mysql si es mysql o sqlite
     * @throws SQLException si hay algún error
     */
    private void insertarPropietarioEjemplo(boolean mysql) throws SQLException {
        Connection connection = null;
        try {
            connection = elegirConexion(mysql);
            connection.setAutoCommit(false);
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(SQLSentences.SQL_INSERTAR_PROPIETARIOS_EJEMPLO);
                connection.commit();
            }
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
                connection.setAutoCommit(true);
            }

        } finally {
            if (connection != null) connection.close();
        }
    }

    /**
     * Esta función va a permitir insertar un propietario en cualquier base de datos,
     * esto dependerá de lo que elija el usuario
     *
     * @param mysql     si quiere que se conecte con MySQL o SQLite
     * @param dni       el dni
     * @param nombre    el nombre
     * @param apellidos los apellidos
     * @param telefono  el teléfono
     * @throws SQLException           lanza la excepción en caso de error
     * @throws ConcesionarioExcepcion lanza la excepción en caso de error
     */
    public void insertarPropietario(boolean mysql, String dni, String nombre, String apellidos, String telefono)
            throws SQLException, ConcesionarioExcepcion {
        if (dni.isEmpty() || nombre.isEmpty() || apellidos.isEmpty()) {
            throw new ConcesionarioExcepcion("Algún campo es inválido");
        }
        // Creamos la conexión
        try (Connection connection = elegirConexion(mysql)) {
            // Creamos el PreparedStatement para insertar propietario
            try (PreparedStatement preparedStatement = connection.prepareStatement(SQLSentences.SQL_INSERTAR_PROPIETARIO)) {
                preparedStatement.setString(1, dni);
                preparedStatement.setString(2, nombre);
                preparedStatement.setString(3, apellidos);
                // En caso de que el teléfono esté vacío se inserta null ya que el campo es nullable
                if (telefono.isEmpty()) preparedStatement.setNull(4, java.sql.Types.NULL);
                else preparedStatement.setString(4, telefono);
                // Ejecutamos la insercción
                preparedStatement.executeUpdate();
            }
        }
    }

    /**
     * Esta función nos va a permitir alternar entre una conexión u otra
     *
     * @param mysql si quiere mysql o sqlite
     * @return la conexión
     * @throws SQLException lanza la excepción en caso de error
     */
    private Connection elegirConexion(boolean mysql) throws SQLException {
        if (mysql) return connCont.getConnectionMySQL();
        else return connCont.getConnectionSQLite();
    }

    /**
     * Esta función va a borrar de la base de datos el coche
     * que corresponda a una matrícula
     *
     * @param matricula la matrícula del coche a buscar
     * @param mysql     si quiere mysql o sqlite
     * @throws ConcesionarioExcepcion lanza la excepción en caso de error
     * @throws SQLException           lanza la excepción en caso de error
     */
    public void borrarCoche(String matricula, boolean mysql) throws ConcesionarioExcepcion, SQLException {
        // Comprobamos que el campo matrícula no esté vacío
        if (matricula.isEmpty()) {
            throw new ConcesionarioExcepcion("La matrícula está vacía");
        }
        // Creamos la conexión
        try (Connection connection = elegirConexion(mysql)) {
            // Obtenemos el coche, en caso de que exista claro
            Coche coche = obtenerCoche(matricula, connection);
            // Se comprueba si existe
            if (coche == null) {
                throw new ConcesionarioExcepcion("El coche que intentas borrar no existe");
            }
            if (coche.getIdPropietario() != 0) {
                throw new ConcesionarioExcepcion("Este coche tiene propietario, no se puede borrar");
            }
            // Creamos el PreparedStatement
            try (PreparedStatement preparedStatement = connection.prepareStatement(SQLSentences.SQL_DELETE_COCHE)) {
                // PreparedStatement -> Le pasamos la matrícula del coche que se quiere eliminar
                preparedStatement.setString(1, matricula);
                // Ejecuto el PreparedStatement
                preparedStatement.executeUpdate();
            }
        }
    }

    /**
     * Esta función va a modificar un coche en concreto dependiendo de la matrícula,
     * se cambiarán los campos que no estén vacíos o sea -1 en caso del precio, si eso
     * es así, significará que se debe mantener el que había
     *
     * @param matricula la matrícula del coche a actualizar
     * @param marca     la marca nueva o no
     * @param modelo    el modelo nuevo o no
     * @param extras    los extras nuevos, o no
     * @param precio    el precio nuevo o no
     * @param mysql     si es mysql o sqlite
     * @throws ConcesionarioExcepcion lanza la excepción en caso de error
     * @throws SQLException           lanza la excepción en caso de error
     */
    public void modificarCoche(String matricula, String marca, String modelo, List<String> extras, double precio, boolean mysql)
            throws ConcesionarioExcepcion, SQLException {
        // Comprobamos que la matrícula sea válida
        if (matricula.isBlank()) {
            throw new ConcesionarioExcepcion("La matrícula no puede estar vacía");
        }
        // Se decide la conexión
        try (Connection connection = elegirConexion(mysql)) {
            // Obtenemos el coche a modificar
            Coche coche = obtenerCoche(matricula, connection);
            // Si el coche es null, lanzamos exception
            if (coche == null) {
                throw new ConcesionarioExcepcion("No se ha encontrado el registro");
            }
            // Creamos el PreparedStatement
            try (PreparedStatement preparedStatement = connection.prepareStatement(SQLSentences.SQL_ACTUALIZAR_COCHE)) {
                if (!marca.isBlank()) preparedStatement.setString(1, marca);
                else preparedStatement.setString(1, coche.getMarca());
                if (!modelo.isBlank()) preparedStatement.setString(2, modelo);
                else preparedStatement.setString(2, coche.getModelo());
                if (!extras.isEmpty()) preparedStatement.setString(3, String.join(", ", extras));
                else {
                    if (!coche.getExtras().isEmpty()) preparedStatement
                            .setString(3, String.join(", ", coche.getExtras()));
                    else preparedStatement.setNull(3, Types.NULL);
                }
                if (precio >= 0) preparedStatement.setDouble(4, precio);
                else preparedStatement.setDouble(4, coche.getPrecio());
                // Le pasamos la matrícula, corresponde al WHERE
                preparedStatement.setString(5, matricula);
                // Ejecutamos el UPDATE
                preparedStatement.executeUpdate();
            }
        }
    }

    /**
     * Esta función va a obtener un coche a partir de una consulta, mediante
     * su matrícula, va a transformarlo en un objeto Coche
     *
     * @param matricula  la matrícula para buscar al coche
     * @param connection la conexión con la que se está trabajando
     * @return el objeto Coche
     * @throws SQLException lanza la excepción en caso de error
     */
    public Coche obtenerCoche(String matricula, Connection connection) throws SQLException {
        // Creamos un PreparedStatement
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQLSentences.SQL_OBTENER_COCHE)) {
            preparedStatement.setString(1, matricula);
            // Obtenemos los campos del coche concreto
            ResultSet resultSet = preparedStatement.executeQuery();
            // Si hay resultado creamos el objeto
            if (resultSet.next()) {
                // Devolvemos el objeto Coche
                return new Coche(resultSet.getString(1), resultSet.getString(2),
                        // Creamos una lista a partir de los extras que haya
                        resultSet.getString(3),
                        resultSet.getString(4) != null ? Arrays.stream(resultSet.getString(4)
                                .split(", ")).map(String::trim).collect(Collectors.toCollection(ArrayList::new))
                                : new ArrayList<>(),
                        resultSet.getDouble(5), resultSet.getInt(6));
            }
        }
        return null;
    }

    /**
     * Esta función se encarga de realizar un traspaso de un vehículo de un concesionario a una persona o
     * de una persona a otra
     *
     * @param matriculaCoche la matrícula del coche a traspasar
     * @param dniComprador   el dni del comprador
     * @param montoEconomico el monto económico
     * @param mysql          el tipo de conexión
     * @throws ConcesionarioExcepcion lanza la excepción en caso de error
     * @throws SQLException           lanza la excepción en caso de error
     */
    public void traspaso(String matriculaCoche, String dniComprador, double montoEconomico, boolean mysql)
            throws ConcesionarioExcepcion, SQLException {
        // Comprobamos que los campos no estén vacíos
        if (matriculaCoche.isBlank() || dniComprador.isBlank() || montoEconomico < 0) {
            throw new ConcesionarioExcepcion("Algún campo no es válido");
        }
        Connection connection = null;
        try {
            // Elegimos la conexión
            connection = elegirConexion(mysql);
            // Ponemos el autocomit a false aquí, lo antes posible por si salta excepción e intenta hacer rollback
            connection.setAutoCommit(false);
            Coche coche = obtenerCoche(matriculaCoche, connection);
            int idComprador = comprobarId(dniComprador, connection);
            // Comprobamos que la matrícula esté asociada a un coche real y lo mismo con el dni del comprador
            if (idComprador == 0 || coche == null) {
                throw new ConcesionarioExcepcion("El comprador y/o el coche no existen");
            }
            /* Llegados a este punto el traspaso se puede realizar con o sin vendedor, es decir concesionario o particular
             * así que vamos a comenzar con las insercciones y actualizaciones */
            // Se inserta el traspaso
            insertarTraspaso(connection, coche, idComprador, montoEconomico);
            // Se actualiza el id_propietario del vehículo
            actualizarIdPropietarioCoche(connection, idComprador, coche.getMatricula());
            // Se hace un commit
            connection.commit();

        } catch (SQLException | ConcesionarioExcepcion e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new ConcesionarioExcepcion(e.getMessage());

        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }

    /**
     * Esta función comprueba a partir de una matrícula si existe o no el comprador o vendedor,
     *
     * @param dni        el dni de la persona. en este caso del comprador
     * @param connection la conexión con la base de datos
     * @return el id del comprador
     * @throws SQLException lanza la excepción en caso de error
     */
    private int comprobarId(String dni, Connection connection) throws SQLException {
        // Creamos un PreparedStatement
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQLSentences.SQL_OBTENER_PROPIETARIO)) {
            preparedStatement.setString(1, dni);
            // Devolvemos el id del comprador en caso de que exista (debe ser distinto de 0)
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    /**
     * Esta función va a insertar un traspaso en la tabla traspasos
     *
     * @param connection     la conexión
     * @param coche          el coche a insertar (la matrícula)
     * @param idComprador    el id del comprador
     * @param montoEconomico el precio de compra del coche
     * @throws SQLException lanza la excepción en caso de error
     */
    private void insertarTraspaso(Connection connection, Coche coche, int idComprador, double montoEconomico)
            throws SQLException {
        // Creamos el PreparedStatement
        try (PreparedStatement preparedStatement = connection
                .prepareStatement(SQLSentences.SQL_INSERTAR_TRASPASO)) {
            // Insertamos los datos
            preparedStatement.setString(1, coche.getMatricula());
            // Comprobamos si compra el coche a un particular o a un concesionario
            if (coche.getIdPropietario() == 0) preparedStatement.setNull(2, Types.NULL);
            else preparedStatement.setInt(2, coche.getIdPropietario());
            preparedStatement.setInt(3, idComprador);
            preparedStatement.setDouble(4, montoEconomico);
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Esta función va a actualizar el id_propietario de coche, esto es para el traspaso
     *
     * @param connection    la conexión a la base de datos
     * @param idPropietario el id nuevo del propietario
     * @param matricula     la matrícula del coche al que hay que actualizarle el campo
     * @throws SQLException lanza la excepción en caso de error
     */
    private void actualizarIdPropietarioCoche(Connection connection, int idPropietario, String matricula)
            throws SQLException {
        // Se crea el PreparedStatement
        try (PreparedStatement ps = connection.prepareStatement(SQLSentences.SQL_ACTUALIZAR_ID_EN_COCHE)) {
            // Se inserta el nuevo valor del propietario
            ps.setInt(1, idPropietario);
            ps.setString(2, matricula);
            ps.executeUpdate();
        }
    }

    /**
     * Esta función va a llamar al procedimiento almacenado con la marca
     * pasada por parámetros
     *
     * @param marca la marca de los vehículos a buscar
     * @return una lista de los coches formateados como Strings
     * @throws SQLException           lanza la excepción en caso de error
     * @throws ConcesionarioExcepcion lanza la excepción en caso de error
     */
    public List<Coche> procedimientoAlmacenado(String marca) throws SQLException, ConcesionarioExcepcion {
        if (marca.isBlank()) {
            throw new ConcesionarioExcepcion("La marca no es válida");
        }
        // Creamos la lista que vamos a devolver
        List<Coche> coches;
        try (Connection connection = elegirConexion(true)) {
            // Creamos el procedimiento almacenado
            crearProcedimientoAlmacenado(connection);
            // Llamamos al procedimiento almacenado con CallableStatment, esta clase es para llamar a los procedimientos
            try (CallableStatement cs = connection.prepareCall(SQLSentences.SQL_LLAMADA_PROCEDIMIENTO_ALMACENADO)) {
                // Introducimos como parámetro del procedimiento la marca
                cs.setString(1, marca);
                // Ejecutamos el procedimiento
                ResultSet rs = cs.executeQuery();
                coches = obtenerListaCoches(rs);
            }
        }
        return coches;
    }

    /**
     * Esta función va a crear un procedimiento almacenado que va a mostrar
     * los coches que sean de una marca concreta, en caso de que exista el
     * procedimiento no pasará nada, ya que se remplazará el procedimiento
     * existente
     *
     * @param connection la conexion con la base de datos
     * @throws SQLException lanza la excepción en caso de error
     */
    private void crearProcedimientoAlmacenado(Connection connection) throws SQLException {
        // Creamos el procedimiento almacenado
        try (Statement statement = connection.createStatement()) {
            // Eliminamos el procedimiento almacenado en caso de que exista
            statement.execute(SQLSentences.SQL_BORRAR_PROCEDIMIENTO_ALMACENADO);
            // Ejecutamos la sentencia para la creación del procedimiento almacenado
            statement.execute(SQLSentences.SQL_PROCEDIMIENTO_ALMACENADO_CREACION);
        }
    }

    /**
     * Esta función va a escribir en .txt un resumen de la base de datos, como
     * el número de coches entre otras cosas
     *
     * @param mysql el tipo de base de datos
     * @throws IOException  lanza la excepción en caso de error
     * @throws SQLException lanza la excepción en caso de error
     */
    public void generarResumen(boolean mysql) throws IOException, SQLException, ConcesionarioExcepcion {
        // Obtenemos todos los coches de la base de datos
        List<Coche> coches = obtenerCoches(mysql);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(prop.getProperty("resumenTXT")))) {
            bw.write("Número de coches: " + numCoches(mysql) + "\n\n");
            bw.write("Coches por marca: \n");
            cochesPorMarca(coches).forEach((s, cocheList) -> {
                try {
                    bw.write(s + "-> " + cocheList + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            bw.write("\n");
            bw.write("Extra más repetido: " + extraMasRepetido(coches));
        } catch (RuntimeException e) {
            throw new ConcesionarioExcepcion("Error al generar resumen: " + e.getMessage());
        }
    }

    /**
     * Esta función va a devolver el número de coches que hay en la base de datos
     *
     * @param mysql si la conexión deber ser mysql o sqlite
     * @return el número de coches
     * @throws SQLException lanza la excepción en caso de error
     */
    private int numCoches(boolean mysql) throws SQLException {
        try (Connection connection = elegirConexion(mysql)) {
            try (Statement statement = connection.createStatement()) {
                ResultSet rs = statement.executeQuery(SQLSentences.SQL_NUM_COCHES);
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Esta función va a devolver una lista de todos los coches
     * de la base de datos
     *
     * @param mysql el tipo de conexión
     * @return una lista de todos los coches
     * @throws SQLException lanza la excepción en caso de error
     */
    private List<Coche> obtenerCoches(boolean mysql) throws SQLException {
        // Una lista de coches
        List<Coche> coches;
        try (Connection connection = elegirConexion(mysql)) {
            try (Statement statement = connection.createStatement()) {
                ResultSet rs = statement.executeQuery(SQLSentences.SQL_OBTENER_COCHES_TODOS);
                // Obtenemos todos los coches de la base de datos y lo guardamos en la lista
                coches = obtenerListaCoches(rs);
            }
        }
        return coches;
    }

    /**
     * Esta función va a devolver un mapa, la clave la marca, los valores,
     * una lista de coches que pertenecen a esa marca
     *
     * @param coches la lista de coches de la base de datos
     * @return lanza la excepción en caso de error
     */
    private Map<String, List<Coche>> cochesPorMarca(List<Coche> coches) {
        return coches.stream().collect(Collectors.groupingBy(Coche::getMarca));
    }

    /**
     * Esta fucnión va a devolver una lista de los extras más repetidos
     *
     * @param coches la lista de todos los coches de la base de datos
     * @return la lista de extras más repetidos
     */
    private List<String> extraMasRepetido(List<Coche> coches) {
        Long maximo = maximo(coches);
        return coches.stream().flatMap(c -> c.getExtras().stream())
                .collect(Collectors.groupingBy(s -> s, Collectors.counting())).entrySet()
                .stream().filter(e -> e.getValue().equals(maximo)).map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Esta función va a devolver el número máximo de la repetición de
     * extras, para posteriormente obtener los extras que más se repitan
     *
     * @param coches la lista de todos los coches de la base de datos
     * @return el valor del máximo
     */
    private Long maximo(List<Coche> coches) {
        return coches.stream().flatMap(c -> c.getExtras().stream())
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()))
                .values().stream().max(Long::compareTo).orElse(0L);
    }
}