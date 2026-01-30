package com.example.GestionTiendaREST.service;

import com.example.GestionTiendaREST.models.Cliente;
import com.example.GestionTiendaREST.models.CompraDTO;
import com.example.GestionTiendaREST.models.Venta;
import com.example.GestionTiendaREST.models.Videojuego;
import com.example.GestionTiendaREST.repository.ClienteRepository;
import com.example.GestionTiendaREST.repository.VentaRepository;
import com.example.GestionTiendaREST.repository.VideojuegoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/* Esta es la clase service donde va toda la lógica de servicio del programa, se le indica con esta etiqueta,
 * el acceso a los datos no va aquí, aquí se llama a los repositorios creados para la manipulación y extracción
 * de estos, AQUÍ SOLO LA LÓGICA DE NEGOCIO, es decir los diferentes comportamientos y condiciones con los datos
 * leídos o modificados */

/* SOLUCIÓN PARA EL AVISO DE LAS ETIQUETAS @Autowired crear el
 constructor aceptando los repositorios y quitar la etiqueta */

/* NOTA IMPORTANTE: la etiqueta @Transactional hace que se tenga el contexto los cambios y demás
 * que ocurra dentro de una función, los objetos/entidades pasan a ser Management por ejemplo al
 * hacer un findBy() sin la etiqueta no habría contexto, solo al hacer save() o delete(), pero no
 * en toda la función */

@Service
public class TiendaService {
    // Esta clase debe implementar los repositorios de los diferentes objetos con los que trabaja
    @Autowired // La etiqueta ejecuta la inyección de dependencias automáticamente sin necesidad del constructor
    private VideojuegoRepository videojuegoRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private VentaRepository ventaRepository;

    /**
     * Esta función va a eliminar todos los datos de todas las tablas,
     * se crea como parte de otra función, la cual si va a crear los datos
     * después de que esta los haya eliminado, la función del repositorio
     * para la eliminación de los datos de una tabla es deleteAll() (realmente
     * he cambiado a deleteAllInBatch(), ya que este borra sin hacer consultas,
     * es decir sin traerse antes con select los datos, lo hace directamente y
     * sin importar las restricciones)
     */
    private void borrarTablas() {
        // Primero se deben de borrar las ventas, ya que son las que dependen de las otras dos entidades
        ventaRepository.deleteAllInBatch();
        // Después eliminamos el resto sin ya importar el orden
        videojuegoRepository.deleteAllInBatch();
        clienteRepository.deleteAllInBatch();
    }

    /**
     * Esta función va a eliminar los datos ya existentes de todas las tablas,
     * posteriormente se crean objetos semillas de ejemplo para trabajar con
     * la base de datos, se crean videojuegos y clientes
     */
    @Transactional // Esta etiqueta indica que esta función va a ser transaccional, es decir se completa o no
    public void datosSemilla() {
        // Borramos todos los datos de todas las tablas
        borrarTablas();
        // Creamos datos de ejemplo en las tablas, con saveAll() metemos un conjunto (Iterable) de entidades para guardar
        // Creamos los videojuegos
        videojuegoRepository.saveAll(
                List.of(new Videojuego(null, "Fallout 4", "Rol", 30, 10, new ArrayList<>()),
                        new Videojuego(null, "Fallout 76", "Rol", 40, 23, new ArrayList<>()),
                        new Videojuego(null, "Fallout New Vegas", "Rol", 19.99, 4, new ArrayList<>())));
        // Creamos los clientes
        clienteRepository.saveAll(List.of(new Cliente(null, "Chelu García", "chelu@gmail.com", 50, new ArrayList<>()),
                new Cliente(null, "Atisbedo", "atis@gmail.com", 30, new ArrayList<>()),
                new Cliente(null, "Respicio Godefrío", "respi@gmail.com", 100, new ArrayList<>())));
    }

    /**
     * Esta función va a devolver una lista con todos los registros
     * de videojuegos que hay en la base de datos
     *
     * @return la lista de videojuegos
     */
    public List<Videojuego> listarVideojuegos() {
        /* La función findAll() devuelve una lista con todas las instancias
         de una entidad en la base de datos, en este caso 'Videojuego' */
        return videojuegoRepository.findAll();
    }

    /**
     * Esta función va a devolver un Optional de un videojuego,
     * esto lo hacemos así para posteriormente valorar si devolver
     * un 404 o un 200
     *
     * @param id el id del videojuego a buscar
     * @return el videojuego envuelto en un optional
     */
    public Optional<Videojuego> obtenerVideojuegoPorId(Long id) {
        /* La función findById() busca por el id del registro y lo devuelve
         * envuelto en un Optional */
        return videojuegoRepository.findById(id);
    }

    /**
     * Esta función va a guardar un videojuego pasado por parámetros
     * en la base de datos comprobando si el stock y el precio son
     * correctos, aparte de que el videojuego no exista ya por el nombre
     * y por el id, CUIDADO CON ESTO PORQUE SI HACEMOS UN save() con el mismo
     * id hará un update
     *
     * @param videojuego el videojuego a crear
     * @return el videojuego creado
     */
    public Videojuego guardarVideojuego(Videojuego videojuego) {
        // Comprobamos que tenga id
        if (videojuego.getId() != null) {
            // Comprobamos si existe el videojuego por el id
            if (obtenerVideojuegoPorId(videojuego.getId()).orElse(null) != null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El videojuego ya existe (mismo id)");
            }
        }
        // Comprobamos si existe un videojuego con este título
        if (videojuegoRepository.existsByTitulo(videojuego.getTitulo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un videojuego con el mismo título");
        }
        // Validamos si precio es menor a 0 o si el stock es menor a 0
        if (videojuego.getPrecio() < 0 || videojuego.getStock() < 0) {
            /* En caso de que entre, lanzamos ResponseStatusException, esta
             * nos permite lanzar una excepción con el código http que le digamos
             * con un mensaje personalizado viene ya controlada de por sí por Spring,
             * este devuelve un JSON detallado del error */
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El precio y/o el stock no son correctos");
        }
        return videojuegoRepository.save(videojuego);
    }

    /**
     * Esta función va a actualizar el stock y/o el precio del videojuego
     * sustituyendo así el videojuego original con los datos originales
     * por un el videojuego con los datos actualizados
     *
     * @param id          el id del videojuego a buscar
     * @param datosNuevos el JSON del videojuego (el videojuego)
     * @return el videojuego actualizado
     */
    @Transactional // Lo hacemos transaccional para que o se realice el proceso completo o nada en caso de excepción
    public Videojuego actualizarVideojuego(Long id, Videojuego datosNuevos) {
        // Obtenemos el videjuego por el id
        Videojuego videojuegoOriginal = obtenerVideojuegoPorId(id)
                // En caso de que no exista lanzamos excepción 404
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el videojuego"));
        // Copiamos los datos nuevos en el original, ignorando copiar los campos señalados, gracias a BeanUtils.copyProperties()
        BeanUtils.copyProperties(datosNuevos, videojuegoOriginal, "id", "titulo", "genero", "ventas");
        // Guardamos el videojuego actualizado, cuando hacemos save() si el id coincide, se sustituye el nuevo por el viejo (entidad)
        return videojuegoRepository.save(videojuegoOriginal);
    }

    /**
     * Esta función va a eliminar un videojuego de la base de datos
     * en caso de que exista y que no tenga ninguna venta asociada
     *
     * @param id el id del videojuego a eliminar
     */
    public void borrarVideojuego(Long id) {
        // Primero obtenemos el videojuego en caso de que exista
        Videojuego videojuego = obtenerVideojuegoPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el videojuego"));
        // En caso de que el videojuego haya sido vendido, lanzamos un 409 (CONFLICT)
        if (!videojuego.getVentas().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Un videojuego vendido no se puede borrar");
        }
        // En caso de que no haya error, se borra con delete()
        videojuegoRepository.delete(videojuego);
    }

    @Transactional
    // Se hacen todas las acciones o ninguna, aparte de esta forma al hacer los sets se cambia en la base de datos
    public Venta comprarVideojuego(CompraDTO compraDTO) {
        // Comprobamos que exista el cliente y el videojuego por su id
        Cliente cliente = clienteRepository.findById(compraDTO.getIdCliente())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el cliente"));
        Videojuego videojuego = obtenerVideojuegoPorId(compraDTO.getIdVideojuego())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el videojuego"));
        // Comprobamos que el stock del videojuego sea igual a 0, devolvemos 400 en caso de que así sea
        if (videojuego.getStock() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El stock del videojuego no puede ser menor a 0");
        }
        // Comprobamos que el saldo del cliente sea menor al precio del videojuego, entonces devolvemos 400
        if (cliente.getSaldo() < videojuego.getPrecio()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El saldo del cliente es insuficiente");
        }
        // Restamos el precio del videojuego al saldo del cliente
        cliente.setSaldo(cliente.getSaldo() - videojuego.getPrecio());
        // Restamos una unidad de stock al videojuego
        videojuego.setStock(videojuego.getStock() - 1);
        // Ahora generamos la venta
        Venta venta = new Venta(null, LocalDate.now(), videojuego.getPrecio(), cliente, videojuego);
        // Guardamos la venta
        ventaRepository.save(venta);
        // Le asignamos la venta al videojuego y cliente
        videojuego.getVentas().add(venta);
        cliente.getVentas().add(venta);
        return venta;
    }

    /**
     * Esta función va a devolver los videojuegos
     * del género pasado por parámetros
     *
     * @param genero el género de los videojuegos a buscar
     * @return una lista con los videojuegos de ese género
     */
    public List<Videojuego> obtenerVideojuegoPorGenero(String genero) {
        // Obtenemos una lista de videojuegos con ese género
        List<Videojuego> videojuegos = videojuegoRepository.findByGenero(genero);
        // En caso de que la lista esté vacía, lanzamos un 404
        if (videojuegos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay videojuegos con el género " + genero);
        }
        return videojuegos;
    }

    /**
     * Esta función va a devolver una lista de videojuegos
     * con un precio menor al pasado por parámetros
     *
     * @param precioLimite el número límite excluyente del precio
     * @return la lista de videojuegos con un precio menor al indicado
     */
    public List<Videojuego> obtenerVideojuegosRangoPrecio(double precioLimite) {
        if (precioLimite < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El precio máximo no puede ser menor a 0");
        }
        // Obtenemos la lista de videojuegos con un precio menor al precioLimite
        List<Videojuego> videojuegos = videojuegoRepository.findVideojuegoByPrecioLessThan(precioLimite);
        if (videojuegos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se han encontrado videojuegos con un precio menor a " + precioLimite);
        }
        return videojuegos;
    }

    /**
     * Esta función va a devolver una lista de ventas
     * de un cliente concreto, en caso de que tenga ventas
     * o el id del cliente exista
     *
     * @param idCliente el id del cliente con dichas ventas
     * @return la lista de ventas del cliente con el id pasado por parámetros
     */
    public List<Venta> obtenerVentasPorIdCliente(long idCliente) {
        // Obtenemos la lista de ventas
        List<Venta> ventas = ventaRepository.findAllByClienteId(idCliente);
        // En caso de que la lista esté vacía, lanzamos un 404
        if (ventas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se han encontrado ventas del cliente con id " + idCliente);
        }
        return ventas;
    }
}