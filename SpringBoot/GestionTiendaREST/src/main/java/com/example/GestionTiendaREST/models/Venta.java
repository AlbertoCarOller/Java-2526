package com.example.GestionTiendaREST.models;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Venta {
    // Creamos el id autogenerado
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Creamos la fecha
    @Column(nullable = false)
    private LocalDate fecha;
    // Creamos el coste
    @Column(nullable = false)
    private double coste;
    // Creamos la relación con cliente
    @ManyToOne()
    // Se pone en este parte el JoinColum porque es el que recibe el campo del otro (el dueño)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
    // Creamos la relación con los videojuegos
    @ManyToOne()
    @JoinColumn(name = "videojuego_id")
    Videojuego videojuego;
}
