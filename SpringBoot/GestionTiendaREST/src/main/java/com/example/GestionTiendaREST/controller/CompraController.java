package com.example.GestionTiendaREST.controller;

import com.example.GestionTiendaREST.models.CompraDTO;
import com.example.GestionTiendaREST.models.Venta;
import com.example.GestionTiendaREST.service.TiendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tienda/comprar") // La URL del recurso
@Tag(name = "Compra", description = "Se realizan compras") // La representación del recurso
public class CompraController {

    @Autowired
    private TiendaService tiendaService;

    /**
     * Esta función va a realizar una venta de un videojuego concreto
     * a un cliente concreto, en caso de que existan
     *
     * @param compraDTO la clase DTO con los ids del videojuego y el cliente (JSON editable)
     * @return devolvemos el JSON de la venta creada con el código 201 en caso de que haya salido bien
     */
    @PostMapping // Se hace un Post porque se está creando una venta
    @Operation(summary = "Realizar venta", description = "Se realiza la venta de un videojuego a un cliente")
    public ResponseEntity<Venta> realizarVenta(@RequestBody CompraDTO compraDTO) {
        return new ResponseEntity<>(tiendaService.comprarVideojuego(compraDTO), HttpStatus.CREATED);
    }
}
