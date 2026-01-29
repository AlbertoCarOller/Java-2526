package com.example.GestionTiendaREST.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    // Generamos el id del Cliente
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Creamos el nombre, no puede ser null
    @Column(nullable = false)
    private String nombre;
    // Creamos el email, este no puede ser null y debe ser único
    @Column(nullable = false, unique = true)
    private String email;
    // Creamos el saldo, este no puede ser null
    @Column(nullable = false)
    private double saldo;
    // Creamos la relación entre un cliente, varias ventas (no es el dueño)
    @OneToMany(mappedBy = "cliente")
    @JsonIgnore
    private List<Venta> ventas;
}
