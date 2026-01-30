package com.example.GestionTiendaREST.controller;

import com.example.GestionTiendaREST.models.Videojuego;
import com.example.GestionTiendaREST.service.TiendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Marca la clase como el controller, aquí se hacen los endpoints
@RequestMapping("/videojuegos") // URL que representa el recurso
@Tag(name = "Videojuegos", description = "Diferentes funciones para tratar los videojuegos")
public class VideojuegoController {

    @Autowired
    private TiendaService tiendaService; // Traemos el servicio para la llamada de las diferentes funciones

    /**
     * Esta función va a devolver una lista de todos
     * los videojuegos registrados en la base de datos
     *
     * @return una lista de videojuegos
     */
    @GetMapping
    @Operation(summary = "Lista de todos los videojuegos",
            description = "Devuelve una lista de todos los videojuegos de la base de datos")
    public ResponseEntity<List<Videojuego>> obtenerVideojuegos() {
        /* En esta función si me deja introducir la función del service
         * directamente en los parámetros de ok(), esto es porque el
         * ResponseEntity en este caso es de una lista de videojuegos,
         * lo que debe de devolver es esto mismo, así que por eso podemos
         * ponerlo así */
        return ResponseEntity.ok(tiendaService.listarVideojuegos());
    }

    /**
     * Esta función va a devolver en un cuerpo JSON, devuelve
     * un videojuego por su id en caso de que exista, en caso de
     * que no lanzará un 404
     *
     * @param id el id del videojuego a buscar
     * @return un JSON con la información del videojuego o
     * lanza el error 404
     */
    @GetMapping("/{id}") // Le pasamos el id que necesitamos introducir por parámetros para la búsqueda
    @Operation(summary = "Videojuego obtenido por id",
            description = "Devuelve un videojuego por el id pasado por el usuario")
    // @PathVariable -> la etiqueta para marcar que sea una variable a introducir en el endpoint
    public ResponseEntity<Videojuego> obtenerVideojuegoPorId(@PathVariable Long id) {
        // Funcionamiento del flujo por dentro con más detalles
        /* tiendaService.obtenerVideojuegoPorId(id).map(videojuego -> ResponseEntity.ok(videojuego))
                .orElse(ResponseEntity.notFound().build()); */
        return tiendaService.obtenerVideojuegoPorId(id)
                // En caso de que exista el videojuego lo devuelve con el código 200
                .map(ResponseEntity::ok)
                // En caso de que no exista devuelve el código 404
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Esta función va a crear un videojuego en caso de que este
     * no se haya creado ya
     *
     * @param videojuego el cuerpo JSON del videojuego (editable)
     * @return el videojuego creado
     */
    @PostMapping // Se hace un post porque se va a crear un registro
    @Operation(summary = "Crear videojuego",
            description = "Crea y devuelve el videojuego creado")
    // @RequestBody -> Significa que lo que le estamos pasando es el cuerpo del JSON (editable desde la API)
    public ResponseEntity<Videojuego> crearVideojuego(@RequestBody Videojuego videojuego) {
        // Retornamos el ResponseEntity con la JSON del videojuego creado y el código 201
        // Con created() no podemos ya que exije una URL directamente
        return new ResponseEntity<>(tiendaService.guardarVideojuego(videojuego), HttpStatus.CREATED);
    }

    /**
     * Esta función va a actualizar un videojuego en caso
     * de que exista, se podrá actualizar el stock y/o el precio
     *
     * @param id          el id del videojuego a actualizar
     * @param datosNuevos el cuerpo JSON del videojuego (editable)
     * @return el videojuego actualizado
     */
    @PutMapping("/{id}") // Se hace un put porque se van a actualizar datos, le pasamos el id del videojuego
    @Operation(summary = "Actualizar videojuego", description = "Actualiza y devuelve el videojuego actualizado")
    public ResponseEntity<Videojuego> actualizarVideojuego(@PathVariable Long id, @RequestBody Videojuego datosNuevos) {
        // Llamamos a la función para el guardado devolviendo así el ResponseEntity con el código 200 en caso de que vaya bien
        return ResponseEntity.ok(tiendaService.actualizarVideojuego(id, datosNuevos));
    }

    /**
     * Esta función va a eliminar un videojuego en caso de que exista,
     * devolviendo el código 200 en caso de que no haya problemas
     *
     * @param id el id del videojuego a eliminar
     * @return la respuesta con el código 200 en caso de que salga bien
     */
    @DeleteMapping("/{id}") // Se hace Delete porque se elimina un videojuego
    @Operation(summary = "Eliminar videojuego",
            description = "Se elimina un videojuego por su id en caso de que no haya sido vendido")
    public ResponseEntity<Void> eliminarVideojuego(@PathVariable Long id) {
        // Borramos el videojuego
        tiendaService.borrarVideojuego(id);
        // Devolvemos el código 200 en caso de que se haya borrado sin problemas
        return ResponseEntity.ok().build();
    }

    /**
     * Esta función va a devolver una lista de todos los videojuegos
     * del género pasado por parámetros
     *
     * @param genero el género de los videojuegos a buscar
     * @return la lista de videojuegos con dicho género
     */
    @GetMapping("/genero/{genero}")
    @Operation(summary = "Lista de videojuegos por género",
            description = "Devuelve una lista de los videojuegos de dicho género")
    public ResponseEntity<List<Videojuego>> obtenerVideojuegoPorGenero(@PathVariable String genero) {
        // Devolvemos la lista de videojuegos
        return ResponseEntity.ok(tiendaService.obtenerVideojuegoPorGenero(genero));
    }

    /**
     * Esta función va a devolver una lista con todos los videojuegos con un precio
     * menor al precio límite pasado por parámetros
     *
     * @param precioMax el precio límite (excluyente)
     * @return el JSON con la lista de videojuegos y el 200 en caso de que salga bien
     */
    @GetMapping("/oferta")
    @Operation(summary = "Obtener videojuegos con precio menor al máximo",
            description = "Devuelve una lista de videojuegos con precio menor al máximo indicado")
    /* @RequestParam -> De esta forma no se le pasa como parte de la URL la variable, si no como parámetro de la misma (?precioLimite=),
     * @PathVariable -> a esta, en cambio, hay que pasarle la variable en la URL */
    public ResponseEntity<List<Videojuego>> obtenerVideojuegosPorPrecioLimite(@RequestParam double precioMax) {
        // Devolvemos el JSON con la lista de videojuegos
        return ResponseEntity.ok(tiendaService.obtenerVideojuegosRangoPrecio(precioMax));
    }
}