package org.example.practicaguiadamongodb;

import org.example.practicaguiadamongodb.model.Alumno;
import org.example.practicaguiadamongodb.repository.AlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class Application implements CommandLineRunner {
    @Autowired
    private AlumnoRepository repositorio;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- INICIANDO SPRING DATA MONGO ---");
        // 1. LIMPIEZA
        repositorio.deleteAll();
        // 2. INSERCIÓN
        // Fíjate que usamos objetos Java, nada de "Documents"
        repositorio.save(new Alumno("Ana", 4.5, false, Arrays.asList("AD", "PSP")));
        repositorio.save(new Alumno("Luis", 8.0, false, Arrays.asList("DI")));
        repositorio.save(new Alumno("Eva", 3.0, true, Arrays.asList("AD")));
        System.out.println("--> Alumnos insertados.");
        // 3. MODIFICACIÓN
        // Estrategia Spring: Buscar -> Modificar Java -> Guardar
        // (Nota: Para updates masivos eficientes se usa MongoTemplate, pero esto es más POO)
        List<Alumno> noRepetidores = repositorio.findByRepetidorFalse();
        for (Alumno a : noRepetidores) {
            a.setNota(a.getNota() + 1.0);
            repositorio.save(a); // Save actúa como Update si tiene ID
        }
        System.out.println("--> Notas actualizadas.");
        // 4. CONSULTA
        System.out.println("\n--- APROBADOS (> 5.0) ---");
        repositorio.findByNotaGreaterThan(5.0)
                .forEach(System.out::println);
    }
}