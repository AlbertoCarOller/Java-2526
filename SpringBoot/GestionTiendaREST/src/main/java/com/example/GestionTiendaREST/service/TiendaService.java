package com.example.GestionTiendaREST.service;

import com.example.GestionTiendaREST.models.Cliente;
import com.example.GestionTiendaREST.models.Videojuego;
import com.example.GestionTiendaREST.repository.ClienteRepository;
import com.example.GestionTiendaREST.repository.VentaRepository;
import com.example.GestionTiendaREST.repository.VideojuegoRepository;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/* Esta es la clase service donde va toda la lógica de servicio del programa, se le indica con esta etiqueta,
 * el acceso a los datos no va aquí, aquí se llama a los repositorios creados para la manipulación y extracción
 * de estos, AQUÍ SOLO LA LÓGICA DE NEGOCIO, es decir los diferentes comportamientos y condiciones con los datos
 * leídos o modificados */

/* SOLUCIÓN PARA EL AVISO DE LAS ETIQUETAS @Autowired crear el
 constructor aceptando los repositorios y quitar la etiqueta */
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
     * correctos
     *
     * @param videojuego el videojuego a crear
     * @return el videojuego creado
     */
    public Videojuego guardarVideojuego(Videojuego videojuego) {
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
        return guardarVideojuego(videojuegoOriginal);
    }
}