package example.best_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import example.best_project.model.Evento;

import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Integer> {

    List<Evento> findByEstado(String estado);

    List<Evento> findByCategoria_IdCategoria(Integer idCategoria);

    List<Evento> findByOrganizador_IdUsuario(Integer idUsuario);
}
