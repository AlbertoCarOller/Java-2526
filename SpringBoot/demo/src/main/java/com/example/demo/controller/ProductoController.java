package com.example.demo.controller;


import com.example.demo.models.Producto;
import com.example.demo.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@Tag(name = "Productos", description = "API de gestión de inventario")
public class ProductoController {

    @Autowired
    private ProductoService service;

    @GetMapping
    @Operation(summary = "Listar todos", description = "Devuelve el listado completo de productos")
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar por ID", description = "Devuelve un producto específico o 404 si no existe")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok) // Si existe, devuelve 200 OK con el producto
                .orElse(ResponseEntity.notFound().build()); // Si no, 404 Not Found
    }

    @PostMapping
    @Operation(summary = "Crear producto", description = "Guarda un nuevo producto en base de datos")
    public ResponseEntity<Producto> guardar(@RequestBody Producto producto) {
        Producto nuevo = service.guardar(producto);
        return new ResponseEntity<>(nuevo, HttpStatus.CREATED); // 201 Created
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.borrar(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
