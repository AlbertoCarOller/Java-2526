package com.example.GestionTiendaREST.repository;

import com.example.GestionTiendaREST.models.Videojuego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/* Ponemos la anotación de repository, esta interfaz es básicamente la que se encarga
 de comunicarse con la base de datos, la que hace los cambios y consultas en ella,
 extiende de la interfaz JpaRepository a la cual hay que especificarle el tipo de dato
 con el que estamos trabajando, en este caso 'Videojuego' y el long es el tipo de la PK
 de esa entidad (clase), de esta forma cuando se llame a las diferentes funciones de esta
 sabe que tipo de objetos devolver y cuando quiere por ejemplo buscar por id, entre otras cosas
 sabe que es un long */
@Repository
public interface VideojuegoRepository extends JpaRepository<Videojuego, Long> {
    /**
     * Esta función gracias a las palabras claves
     * de Spring busca un videojuego por su título
     *
     * @param titulo el título del videojuego
     * @return un boolean para saber si existe o no
     */
    boolean existsByTitulo(String titulo);
}
