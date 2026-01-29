package com.example.GestionTiendaREST.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
// Esta es una clase DTO, mezcla los atributos de dos clases utilizando solo los que queramos
public class CompraDTO {
    // Guardamos el id del cliente que va a realizar la compra
    private Long idCliente;
    // Guardamos el id del videojuego que va a ser comprado
    private Long idVideojuego;
}
