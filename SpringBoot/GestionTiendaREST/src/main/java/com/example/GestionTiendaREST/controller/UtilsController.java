package com.example.GestionTiendaREST.controller;

import com.example.GestionTiendaREST.service.TiendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Le dice a Spring que hay un controller y debe gestionarlo con llamadas HTTP
@RestController
// Esta URL representa un recurso de la API
@RequestMapping("/utils/seed") // URL: http://localhost:8081/swagger-ui/index.html
// El nombre y descripción del recurso de la API
@Tag(name = "Semilla", description = "Se borran los datos de las bases de datos y se crean datos de ejemplo")
public class UtilsController {

    // La clase que gestiona el servicio de negocio
    @Autowired
    private TiendaService tiendaService;

    /* IMPORTANTE: el build() sabe construir el ResponseEntity del tipo que corresponda sin
       pararle nada por parámetros a la función específica, al pasarla por parámetros no es necesario
       el build porque sabe que pasarle, a menos que sea Void, aparte si vamos a devolver algo como noContent()
       como aquí no vamos a devolver nada, hay que hacer el build obligatoriamente */

    /**
     * Esta función va a borrar los datos de todas las tablas,
     * para así posteriormente crear datos de prueba de videojuegos
     * y de clientes
     *
     * @return nada, un JSON vacío, sin respuesta
     */
    @GetMapping // Es un GET porque no se pasa nada por parámetros al endpoint
    @Operation(summary = "Crear datos semilla", description = "Se borran los datos de la base de datos y crea datos de ejemplo")
    // Void -> Porque en el JSON no devuelve nada en el cuerpo
    public ResponseEntity<Void> crearDatosSemilla() {
        // Borra y crea los datos semilla
        tiendaService.datosSemilla();
        /* Devolvemos un código 204 para indicar que ha salido bien y el cuerpo no devuelve nada,
         por lo tanto, el ResponseEntity es Void */
        return ResponseEntity.noContent().build();
    }
}
