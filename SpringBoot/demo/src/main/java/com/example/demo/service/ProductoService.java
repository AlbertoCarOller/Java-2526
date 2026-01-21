package com.example.demo.service;

import com.example.demo.models.Producto;
import com.example.demo.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository repositorio;

    // Obtener todos
    public List<Producto> listarTodos() {
        return repositorio.findAll();
    }

    // Obtener por ID
    public Optional<Producto> buscarPorId(Long id) {
        return repositorio.findById(id);
    }

    // Crear o Actualizar con validación
    @Transactional
    public Producto guardar(Producto producto) {
        // Regla de negocio: Precio no puede ser negativo
        if (producto.getPrecio() != null && producto.getPrecio() < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        // Regla de negocio: Stock inicial por defecto si es nulo
        if (producto.getStock() == null) {
            producto.setStock(0);
        }
        return repositorio.save(producto);
    }

    // Borrar
    @Transactional
    public void borrar(Long id) {
        repositorio.deleteById(id);
    }

    // Buscar por nombre (usa el método custom del repo)
    public List<Producto> buscarPorNombre(String texto) {
        return repositorio.findByNombreContaining(texto);
    }
}
