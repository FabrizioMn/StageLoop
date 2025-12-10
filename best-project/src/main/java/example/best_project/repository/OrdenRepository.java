package example.best_project.repository;

import example.best_project.model.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrdenRepository extends JpaRepository<Orden, Integer> {
    List<Orden> findByUsuario_IdUsuarioOrderByFechaOrdenDesc(Integer idUsuario);
    List<Orden> findTop5ByOrderByFechaOrdenDesc();
}