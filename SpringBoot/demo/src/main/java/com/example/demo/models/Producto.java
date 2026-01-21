package com.example.demo.models;

import jakarta.persistence.*;
import lombok.Data; // Genera getters, setters, toString, equals automáticamente
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "productos")
@Data // Lombok
@NoArgsConstructor // Constructor vacío requerido por JPA
@AllArgsConstructor // Constructor con todos los campos
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private Double precio;

    private Integer stock;

    // Si no usas Lombok, aquí tendrías que generar Getters y Setters manualmente
}
