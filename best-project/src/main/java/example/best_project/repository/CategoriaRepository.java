package example.best_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import example.best_project.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    boolean existsByNombreCategoria(String nombreCategoria);
}