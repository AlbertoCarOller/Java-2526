package com.example.GestionTiendaREST.service;

import com.example.GestionTiendaREST.models.Cliente;
import com.example.GestionTiendaREST.models.Videojuego;
import com.example.GestionTiendaREST.repository.ClienteRepository;
import com.example.GestionTiendaREST.repository.VentaRepository;
import com.example.GestionTiendaREST.repository.VideojuegoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/* Esta es la clase service donde va toda la lógica de servicio del programa, se le indica con esta etiqueta,
 * el acceso a los datos no va aquí, aquí se llama a los repositorios creados para la manipulación y extracción
 * de estos, AQUÍ SOLO LA LÓGICA DE NEGOCIO, es decir los diferentes comportamientos y condiciones con los datos
 * leídos o modificados */

/* SOLUCIÓN PARA EL AVISO DE LAS ETIQUETAS @Autowired crear el
 constructor aceptando los repositorios y quitar la etiqueta */
@Service
public class TiendaService {
    // Esta clase debe implementar los repositorios de los diferentes objetos con los que trabaja
    @Autowired // La etiqueta ejecuta la inyección de dependencias automáticamente sin necesidad del constructor
    private VideojuegoRepository videojuegoRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private VentaRepository ventaRepository;

    /**
     * Esta función va a eliminar todos los datos de todas las tablas,
     * se crea como parte de otra función, la cual si va a crear los datos
     * después de que esta los haya eliminado, la función del repositorio
     * para la eliminación de los datos de una tabla es deleteAll()
     */
    private void borrarTablas() {
        // Primero se deben de borrar las ventas, ya que son las que dependen de las otras dos entidades
        ventaRepository.deleteAll();
        // Después eliminamos el resto sin ya importar el orden
        videojuegoRepository.deleteAll();
        clienteRepository.deleteAll();
    }

    /**
     * Esta función va a eliminar los datos ya existentes de todas las tablas,
     * posteriormente se crean objetos semillas de ejemplo para trabajar con
     * la base de datos, se crean videojuegos y clientes
     */
    @Transactional
    public void datosSemilla() {
        // Borramos todos los datos de todas las tablas
        borrarTablas();
        // Creamos datos de ejemplo en las tablas, con saveAll() metemos un conjunto (Iterable) de entidades para guardar
        // Creamos los videojuegos
        videojuegoRepository.saveAll(
                List.of(new Videojuego(null, "Fallout 4", "Rol", 30, 10, new ArrayList<>()),
                        new Videojuego(null, "Fallout 76", "Rol", 40, 23, new ArrayList<>()),
                        new Videojuego(null, "Fallout New Vegas", "Rol", 19.99, 4, new ArrayList<>())));
        // Creamos los clientes
        clienteRepository.saveAll(List.of(new Cliente(null, "Chelu García", "chelu@gmail.com", 50, new ArrayList<>()),
                new Cliente(null, "Atisbedo", "atis@gmail.com", 30, new ArrayList<>()),
                new Cliente(null, "Respicio Godefrío", "respi@gmail.com", 100, new ArrayList<>())));
    }
}
