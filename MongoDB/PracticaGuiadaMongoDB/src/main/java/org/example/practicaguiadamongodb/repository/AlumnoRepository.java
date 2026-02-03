package org.example.practicaguiadamongodb.repository;

import org.example.practicaguiadamongodb.model.Alumno;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

// <TipoEntidad, TipoID>
public interface AlumnoRepository extends MongoRepository<Alumno, String> {
    // Método mágico: Spring implementa esto solo viendo el nombre
    // SELECT * FROM alumnos WHERE nota > ?
    List<Alumno> findByNotaGreaterThan(Double notaCorte);

    // Método para buscar no repetidores
    List<Alumno> findByRepetidorFalse();
}