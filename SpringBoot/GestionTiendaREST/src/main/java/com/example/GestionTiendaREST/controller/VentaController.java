package com.example.GestionTiendaREST.controller;

import com.example.GestionTiendaREST.models.Venta;
import com.example.GestionTiendaREST.service.TiendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ventas") // La URL del recurso
@Tag(name = "Ventas", description = "Se obtienen ventas")
public class VentaController {

    @Autowired
    private TiendaService tiendaService;

    /**
     * Esta función va a devolver un JSON con las ventas
     * de los clientes con el id pasado por parámetros
     *
     * @param idCliente el id del cliente
     * @return el JSON con la lista de ventas y 200 si sale bien
     */
    @GetMapping("/cliente/{idCliente}")
    @Operation(summary = "Obtener ventas de cliente",
            description = "Devuelve una lista de ventas del cliente con el id")
    public ResponseEntity<List<Venta>> obtenerVentasPorIdCliente(@PathVariable long idCliente) {
        // Devolvemos la lista de ventas
        return ResponseEntity.ok(tiendaService.obtenerVentasPorIdCliente(idCliente));
    }
}
