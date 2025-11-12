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
                id_vendedor     INT NOT NULL,
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
                id_vendedor     INTEGER NOT NULL,
                id_comprador    INTEGER NOT NULL,
                monto_economico NUMERIC(10, 2) NOT NULL,
                FOREIGN KEY (matricula_coche) REFERENCES coches(matricula),
                FOREIGN KEY (id_vendedor)     REFERENCES propietarios(id_propietario),
                FOREIGN KEY (id_comprador)    REFERENCES propietarios(id_propietario)
            );""";
    // Insercciones de ejemplo al crear las tablas, las sentencias inserts básicas son válidas para MySQL y SQLite
    public static final String SQL_INSERT_PROPIETARIOS_SQL = """
            INSERT INTO propietarios (dni, nombre, apellidos, telefono) VALUES
            ('11111111A', 'Ana', 'García López', '611111111'),
            ('22222222B', 'Luis', 'Martínez Pérez', '622222222'),
            ('33333333C', 'Carlos', 'Sánchez Ruiz', '633333333');
            """;
    public static final String SQL_INSERT_COCHES_SQL = """
            INSERT INTO coches (matricula, marca, modelo, extras, precio, id_propietario) VALUES
            ('1234ABC', 'Seat', 'Ibiza', 'Aire Acondicionado', 18000.50, 1),
            ('5678DEF', 'Ford', 'Focus', 'Navegador|Techo solar', 22500.00, 2);
            """;
}
