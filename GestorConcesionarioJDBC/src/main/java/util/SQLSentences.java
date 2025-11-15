package util;

public class SQLSentences {
    public SQLSentences() {
    }

    // Los atributos de la clase son las sentencias SQL, estas setencias al ser estáticas se llaman sin crear la instancia del objeto
    // Esta sentencia crea el esquema vacío de la base de datos MySQL
    public static final String SQL_CREATE_SCHEME_MYSQL = "CREATE DATABASE IF NOT EXISTS concesionario_db";
    // Estas sentencias crean las tablas de la base de datos de MySQL
    public static final String SQL_CREATE_TABLE_PROPIETARIOS_MYSQL = """
            CREATE TABLE IF NOT EXISTS propietarios (
                id_propietario INT PRIMARY KEY AUTO_INCREMENT,
                dni            VARCHAR(10) NOT NULL UNIQUE,
                nombre         VARCHAR(100) NOT NULL,
                apellidos      VARCHAR(150) NOT NULL,
                telefono       VARCHAR(15)
            );""";
    public static final String SQL_CREATE_TABLE_COCHES_MYSQL = """
            CREATE TABLE IF NOT EXISTS coches (
                matricula       VARCHAR(10) PRIMARY KEY,
                marca           VARCHAR(50) NOT NULL,
                modelo          VARCHAR(50) NOT NULL,
                extras          VARCHAR(255),
                precio          DECIMAL(10, 2) NOT NULL,
                id_propietario  INT,
                FOREIGN KEY (id_propietario) REFERENCES propietarios(id_propietario)
            );""";
    public static final String SQL_CREATE_TABLE_TRASPASOS_MYSQL = """
            CREATE TABLE IF NOT EXISTS traspasos (
                id_traspaso     INT PRIMARY KEY AUTO_INCREMENT,
                matricula_coche VARCHAR(10) NOT NULL,
                id_vendedor     INT,
                id_comprador    INT NOT NULL,
                monto_economico DECIMAL(10, 2) NOT NULL,
                FOREIGN KEY (matricula_coche) REFERENCES coches(matricula),
                FOREIGN KEY (id_vendedor)     REFERENCES propietarios(id_propietario),
                FOREIGN KEY (id_comprador)    REFERENCES propietarios(id_propietario)
            );""";
    // Estas sentencias crean las tablas de SQLite
    public static final String SQL_CREATE_TABLE_PROPIETARIOS_MYSQLITE = """
            CREATE TABLE IF NOT EXISTS propietarios (
                id_propietario INTEGER PRIMARY KEY AUTOINCREMENT,
                dni            TEXT(10) NOT NULL UNIQUE, -- Añadido UNIQUE
                nombre         TEXT(100) NOT NULL,
                apellidos      TEXT(150) NOT NULL,
                telefono       TEXT(15)
            );""";
    public static final String SQL_CREATE_TABLE_COCHES_MYSQLITE = """
            CREATE TABLE IF NOT EXISTS coches (
                matricula       TEXT(10) PRIMARY KEY,
                marca           TEXT(50) NOT NULL,
                modelo          TEXT(50) NOT NULL,
                extras          TEXT(255),
                precio          NUMERIC(10, 2) NOT NULL, -- SQLite usa NUMERIC o REAL
                id_propietario  INTEGER,
                FOREIGN KEY (id_propietario) REFERENCES propietarios(id_propietario)
            );""";
    public static final String SQL_CREATE_TABLE_TRASPASOS_MYSQLITE = """
            CREATE TABLE IF NOT EXISTS traspasos (
                id_traspaso     INTEGER PRIMARY KEY AUTOINCREMENT,
                matricula_coche TEXT(10) NOT NULL,
                id_vendedor     INTEGER,
                id_comprador    INTEGER NOT NULL,
                monto_economico NUMERIC(10, 2) NOT NULL,
                FOREIGN KEY (matricula_coche) REFERENCES coches(matricula),
                FOREIGN KEY (id_vendedor)     REFERENCES propietarios(id_propietario),
                FOREIGN KEY (id_comprador)    REFERENCES propietarios(id_propietario)
            );""";
    // Insercciones de coches, las sentencias inserts básicas son válidas para MySQL y SQLite
    public static final String SQL_INSERT_COCHE_SQL = """
            INSERT INTO coches(matricula, marca, modelo, extras, precio) VALUES (?, ?, ?, ?, ?)
            """;
    // Consulta, muestra un listado de los coches que no tienen propietarios
    public static final String SQL_COCHES_SIN_PROPIETARIO = """
            select *
            from coches c
            where c.id_propietario is NULL;""";
    // Consulta, muestra un listado de los coches que tienen propietarios
    public static final String SQL_COCHES_CON_PROPIETARIO = """
            select *
            from coches c
            where c.id_propietario is NOT NULL;""";
    // Consulta, muestra un listado de los coches que tienen propietarios
    public static final String SQL_COCHES_CON_PROPIETARIO_JOIN = """
            select c.matricula, c.marca, c.modelo, c.extras, c.precio
            from coches c
            join propietarios p on c.id_propietario = p.id_propietario;""";
    // Insertar propietario
    public static final String SQL_INSERTAR_PROPIETARIO = """
            insert into propietarios(dni, nombre, apellidos, telefono) values (?, ?, ?, ?);""";
    // Borrar coche por matrícula
    public static final String SQL_DELETE_COCHE = """
            delete
            from coches
            where matricula like ?;
            """;
    // Consuta que devuelve un coche buscado por la matrícula
    public static final String SQL_OBTENER_COCHE = """
            select *
            from coches
            where matricula like ?;""";
    // Actualizamos los campos de un Coche
    public static final String SQL_ACTUALIZAR_COCHE = """
            update coches
            set
            	marca = ?,
                modelo = ?,
                extras = ?,
                precio = ?
            where matricula = ?;""";
    // Consulta que devuelve un propietario
    public static final String SQL_OBTENER_PROPIETARIO = """
            select id_propietario
            from propietarios
            where dni like ?""";
    // Inserta datos en un traspaso
    public static final String SQL_INSERTAR_TRASPASO = """
            insert into traspasos(matricula_coche, id_vendedor, id_comprador, monto_economico)
             values(?, ?, ?, ?);""";
    // Sentencia que actualiza el id_propietario de coches (Se utiliza para el traspaso)
    public static final String SQL_ACTUALIZAR_ID_EN_COCHE = """
            update coches
            set id_propietario = ?
            where matricula = ?;
            """;
}
