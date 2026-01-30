package com.example.GestionTiendaREST.repository;

import com.example.GestionTiendaREST.models.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    /**
     * Esta función va a buscar todas las ventas
     * con el cliente id pasado por parámetros
     *
     * @param clienteId el id del cliente con dichas ventas
     * @return la lista de ventas con el id del cliente pasado por parámetros
     */
    List<Venta> findAllByClienteId(long clienteId);
}
