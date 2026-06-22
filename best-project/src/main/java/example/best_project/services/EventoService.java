package example.best_project.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import example.best_project.event.domain.Evento;

public interface EventoService {

    List<Evento> listarEventos();
    List<Evento> listarEventosActivos();
    List<Evento> listarEventosPendientes();
    Evento guardarEvento(Evento evento);
    Evento obtenerEventoPorId(Integer id);
    Evento actualizarEvento(Integer id,Evento eventoActualizado,MultipartFile archivoImagen);
    void eliminarEvento(Integer id);
    void aprobarEvento(Integer id);
    void rechazarEvento(Integer id);
    List<Evento> listarPorOrganizador(Integer idUsuario);
    
}
