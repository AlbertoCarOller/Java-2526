package com.example.GestionTiendaREST.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/* La etiqueta @RestControllerAdvice crea una red de seguridad gigante,
 * cuando ocurra una excepción en vez de mostrárselo al usuario directamente
 * sin atraparla, lo que hace es atraparlas automáticamente sin necesidad de
 * usar los try-catch, se la asignamos a una clase que va a ser la encargada
 * de atrapar las excepciones en sí */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // DIFERENTES CÓDIGOS DE RESPUESTA HTTP: https://developer.mozilla.org/es/docs/Web/HTTP/Reference/Status

    // ESQUEMATIZACIÓN DE LOS CÓDIGOS DE ERROR HTTP
    // BAD_REQUEST -> 400, significa que lo que se ha solicitado es imposible de dar (el que utilizamos aquí)
    // INTERNAL_SERVER_ERROR -> 500, el servidor no funciona, ha explotado
    // NOT_FOUND -> 404, no se encuentra lo solicitado

    /* La etiqueta @ExceptionHandler hace que spring se quede vigilando hasta que se haya
     * lanzado una excepción del tipo 'IllegalArgumentException' cuando pase ejecuta
     * esta función en lugar de la acción real que debía pasar */
    @ExceptionHandler(IllegalArgumentException.class)
    /**
     * Esta función va a atrapar excepciones, el ResponseEntity<> es una caja la cual debe
     * enviar los datos a la llamada http cuando hay un error se crea un ResponseEntity con
     * un Map<String, String> esto lo hacemos para enviar un JSON, el código http que devolverá
     * será el 400, esta excepción ocurre cuando se introducen parámetros incorrectos cuando se
     * utilizan los diferentes endpoints de http, de la API
     * @param e la IllegalArgumentException
     * @return la respuesta JSON para el servidor http
     */
    public ResponseEntity<Map<String, String>> argumentoInvalido(IllegalArgumentException e) {
        // Creamos el mapa que va a enviar los datos en formato JSON
        Map<String, String> error = new HashMap<>();
        // Introducimos un mensaje genérico para señalar que algo ha salido mal
        error.put("error", "Solicitud inválida");
        // Introducimos el mensaje de error concreto
        error.put("mensaje", e.getMessage());
        // Devuelve el JSON con body() a la llamada http -> {"error" : "Solicitud inválida, "mensaje" : "mensaje específico de error"}
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Esta función va a atrapar todas las excepciones internas que puede
     * provocar la base de datos, es decir tanto de lectura como de escritura,
     * el código de error http que devuelve es el 500
     *
     * @param e la DataAccessException
     * @return la respuesta JSON para el servidor http
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, String>> errorServidor(DataAccessException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Error interno del servidor");
        error.put("mensaje", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
