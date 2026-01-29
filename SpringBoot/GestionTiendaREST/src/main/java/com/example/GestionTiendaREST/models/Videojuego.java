package com.example.GestionTiendaREST.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data // Importamos lombok para la creación de los constructores y getters y setters de forma automática
@NoArgsConstructor // El constructor sin argumentos para poder crear la tabla (obligatorio con JPA)
@AllArgsConstructor // El constructor para todos los campos
public class Videojuego {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Creamos el id de cada videojuego (PK) se crea de forma auto-generada
    /* El campo 'título' no podrá ser null y tendrá una longitud máxima de 150 caractéres, de
     * esta forma tiene sentido que no puede haber un videojuego sin título */
    @Column(nullable = false, length = 150, unique = true)
    private String titulo;
    // El género tampoco puede ser null y le ponemos una longitud máxima de 50
    @Column(nullable = false, length = 50)
    private String genero;
    // El precio no puede ser null, ya que un videojuego debe tener un precio
    @Column(nullable = false)
    private double precio;
    // El stock lo ponemos como no null, debe de tener un stock
    @Column(nullable = false)
    private int stock;
    // La relación de un videojuego tiene muchas ventas
    @OneToMany(mappedBy = "videojuego")
    @JsonIgnore /* @JsonIgnore -> Añadimos esta anotación para que al crear el JSON de la venta ignore
     la lista de ventas, pa que no se llame así misma creando un bucle infinito */
    private List<Venta> ventas;

    // AQUÍ DEBERÍAN DE ESTAR LOS GETTERS Y SETTERS AL IGUAL QUE LOS CONSTRUCTORES SI NO UTILIZAREMOS 'LOMBOK'
}
