package service;

import PersonalExceptions.ConcesionarioExcepcion;
import model.Coche;
import util.ConfigLoader;
import util.ConnectionController;
import util.SQLSentences;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class ConcensionarioService {
    private ConnectionController connCont;
    private Properties prop;

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
     * @return true si la conexión funciona
     * @throws SQLException en caso de que la conexión no funcione, por lo que no devolverá nada
     */
    private Boolean comprobarConexionMySQL() throws SQLException {
        // Se crea el esquema para comprobar la conexión
        crearEsquema();
        try (Connection connection = connCont.getConnectionMySQL()) {
        }
        // Si no ha saltado excepción devolverá true
        return true;
    }

    /**
     * Se comprueba que la conexión se ha realizado correctamente con SQLite
     *
     * @return true si la conexión funciona
     * @throws SQLException en caso de que la conexión no funcione, por lo que no devolverá nada
     */
    private Boolean comprobarConexionSQLite() throws SQLException {
        try (Connection connection = connCont.getConnectionSQLite()) {
        }
        // Si no ha saltado excepción devolverá true
        return true;
    }

    /**
     * Esta función va a crear el esquema de la base de datos,
     * en caso de que ya exista la base de datos, la elimina
     *
     * @throws SQLException
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
     * @throws SQLException
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
     * @throws SQLException
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
     * @throws SQLException
     */
    private void insertarDatosCSV(List<Coche> coches, Boolean mysql) throws SQLException {
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
     * @throws IOException
     */
    private List<Coche> importarCochesCSV() throws IOException {
        // Creamos la lista a devolver
        List<Coche> coches = new ArrayList<>();
        // Obtenemos todas las líneas del fichero
        List<String> lineas = Files.readAllLines(Path.of(prop.getProperty("pathCSV")));
        // Quitamos la primera hora
        lineas.removeFirst();
        lineas.forEach(line -> {
            // Lista de los campos
            List<String> campos = Arrays.stream(line.split(";"))
                    .filter(c -> !c.isBlank()).toList();
            // Si hay 4 campos significa que no hay extras
            if (campos.size() == 4) {
                coches.add(new Coche(campos.getFirst().trim(), campos.get(1).trim(), campos.get(2).trim(), new ArrayList<>(),
                        Double.parseDouble(campos.getLast().trim()), 0));

                // Si hay 5 campos significa que tiene extras
            } else if (campos.size() == 5) {
                coches.add(new Coche(campos.getFirst().trim(), campos.get(1).trim(), campos.get(2).trim(),
                        Arrays.stream(campos.get(3).trim().split("\\|")).map(String::trim).collect(Collectors
                                .toCollection(ArrayList::new)), Double.parseDouble(campos.getLast().trim()), 0));
            }
        });
        return coches; // -> Devolvemos la lista con los coches ya formados
    }

    /**
     * Esta función recoge todas las acciones que debe hacer la primera opción del menú
     * dependiendo del tipo de conexión que se quiera se hará de una forma u otra
     *
     * @param mysql si la conexión es mysql o no
     * @return true si la conexión es correcta
     * @throws SQLException
     * @throws IOException
     */
    public boolean iniciarDataBase(Boolean mysql) throws SQLException, IOException {
        Boolean conexionCorrecta; // Si la conexión es correcta
        // Si nos queremos conectar con MySQL
        if (mysql) {
            conexionCorrecta = comprobarConexionMySQL();
            crearTablasMySQL(); // -> Creamos las tablas MySQL
            insertarDatosCSV(importarCochesCSV(), true); // -> Insertamos los datos del CSV
            // Nos conectamos con SQLite
        } else {
            conexionCorrecta = comprobarConexionSQLite();
            crearTablasSQLite(); // -> Creamos las tablas SQLite
            insertarDatosCSV(importarCochesCSV(), false); // -> Insertamos los datos del CSV
        }
        return conexionCorrecta;
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
     * @throws ConcesionarioExcepcion
     * @throws SQLException
     */
    public void insertarCoche(String matricula, String marca, String modelo, List<String> extras, double precio, boolean mysql)
            throws ConcesionarioExcepcion, SQLException {
        // Comprobamos si los campos son correctos
        if (marca.isEmpty() || modelo.isEmpty() || matricula.isEmpty()) {
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
     * Esta función va a devolver una lista de Strings con los elementos
     * de los coches, dependiendo de lo que quiera el usuario se elegirá
     * la conexión y se elegirá si quiere que se muestre los coches con o sin
     * propietarios
     *
     * @param mysql        la conexión
     * @param propietarios con o sin propietarios
     * @return una lista de coches (Strings)
     * @throws SQLException
     */
    public List<String> listarCoches(boolean mysql, boolean propietarios) throws SQLException {
        // La lista de coches a devolver como Strings para no transformarlos a objetos
        List<String> coches = new ArrayList<>();
        try (Connection connection = elegirConexion(mysql)) {
            // Preparamos el Statement
            try (Statement statement = connection.createStatement()) {
                String sqlDecidida;
                // Dependiendo de si queremos mostrar con o sin propietarios se seleccionará un setencia u otra
                if (propietarios) sqlDecidida = SQLSentences.SQL_COCHES_CON_PROPIETARIO_JOIN;
                else sqlDecidida = SQLSentences.SQL_COCHES_SIN_PROPIETARIO;
                // El ResultSet se cierra solo cuando su Statement se cierra
                ResultSet rs = statement.executeQuery(sqlDecidida);
                while (rs.next()) {
                    coches.add(String.join(" | ", rs.getString(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4) == null ? "Sin extras" : rs.getString(4),
                            String.valueOf(rs.getDouble(5)),
                            rs.getInt(6) == 0 ? "Sin propietario" : String.valueOf(rs.getInt(6))));
                }
            }
        }
        return coches;
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
     * @throws SQLException
     * @throws ConcesionarioExcepcion
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
     * @throws SQLException
     */
    private Connection elegirConexion(boolean mysql) throws SQLException {
        if (mysql) return connCont.getConnectionMySQL();
        else return connCont.getConnectionSQLite();
    }

    /**
     * Esta función va borrar de la base de datos el coche
     * que corresponda a una matrícula
     *
     * @param matricula la matrícula del coche a buscar
     * @param mysql     si quiere mysql o sqlite
     * @throws ConcesionarioExcepcion
     * @throws SQLException
     */
    public void borrarCoche(String matricula, boolean mysql) throws ConcesionarioExcepcion, SQLException {
        // Comprobamos que el campo matrícula no esté vacío
        if (matricula.isEmpty()) {
            throw new ConcesionarioExcepcion("La matrícula está vacía");
        }
        // Creamos la conexión
        try (Connection connection = elegirConexion(mysql)) {
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
     * @throws ConcesionarioExcepcion
     * @throws SQLException
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
     * @throws SQLException
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
                        !resultSet.getString(4).isBlank() ? Arrays.stream(resultSet.getString(4)
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
     * @throws ConcesionarioExcepcion
     * @throws SQLException
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
     * @throws SQLException
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
     * @throws SQLException
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
     * @throws SQLException
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
}