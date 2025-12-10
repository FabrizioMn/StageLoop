package example.best_project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import example.best_project.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario,Integer> {
    Optional<Usuario> findByEmail(String email);

    boolean existsByNumTelefono(String numTelefono);

    List<Usuario> findByRol_NombreRol(String nombrerol);
    
}
