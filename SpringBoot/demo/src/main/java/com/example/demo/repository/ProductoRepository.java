package com.example.demo.repository;

import com.example.demo.models.Producto;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Método mágico: Spring crea la consulta basada en el nombre
    // SELECT * FROM productos WHERE nombre LIKE %texto%
    List<Producto> findByNombreContaining(String texto);

    // Método mágico: Spring crea la consulta basada en el precio
    // SELECT * FROM productos WHERE precio < ?
    List<Producto> findByPrecioLessThan(Double precio);

    // Consulta personalizada usando JPQL (orientado a objetos)
    @Query("SELECT p FROM Producto p WHERE p.stock < 10")
    List<Producto> buscarStockBajo();
}
