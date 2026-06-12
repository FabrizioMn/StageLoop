package example.best_project.services.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import example.best_project.model.Evento;
import example.best_project.model.Rol;
import example.best_project.model.Usuario;
import example.best_project.repository.EventoRepository;
import example.best_project.repository.RolRepository;
import example.best_project.repository.UsuarioRepository;
import example.best_project.services.EventoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventoServiceImpl implements EventoService {

    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    @Override
    public List<Evento> listarEventos() {
        return eventoRepository.findAll();
    }

    @Override
    public List<Evento> listarEventosActivos() {
        return eventoRepository.findByEstado("Activo");
    }

    @Override
    public Evento guardarEvento(Evento evento) {
        if (evento.getPrecio() == null || evento.getPrecio().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("El precio no puede ser negativo");
        }
        return eventoRepository.save(evento);
    }

    @Override
    @Transactional
    public Evento actualizarEvento(Integer id, Evento eventoActualizado, MultipartFile archivoImagen) {
        Evento eventoExistente = obtenerEventoPorId(id);

        eventoExistente.setTitulo(eventoActualizado.getTitulo());
        eventoExistente.setDescripcion(eventoActualizado.getDescripcion());
        eventoExistente.setFechaEvento(eventoActualizado.getFechaEvento());
        eventoExistente.setHoraInicio(eventoActualizado.getHoraInicio());
        eventoExistente.setHoraFin(eventoActualizado.getHoraFin());
        eventoExistente.setLugarEvento(eventoActualizado.getLugarEvento());
        eventoExistente.setPrecio(eventoActualizado.getPrecio());
        eventoExistente.setCapacidad(eventoActualizado.getCapacidad());

        if (eventoActualizado.getCategoria() != null) {
            eventoExistente.setCategoria(eventoActualizado.getCategoria());
        }
        if (eventoActualizado.getEstado() != null) {
            eventoExistente.setEstado(eventoActualizado.getEstado());
        }

        try {
            if (archivoImagen != null && !archivoImagen.isEmpty()) {
                eventoExistente.setImagen(archivoImagen.getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al procesar la imagen", e);
        }

        return eventoRepository.save(eventoExistente);
    }

    @Override
    public Evento obtenerEventoPorId(Integer id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado con el id:" + id));
    }

    @Override
    public void eliminarEvento(Integer id) {
        if (!eventoRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar, evento no encontrado");
        }
        eventoRepository.deleteById(id);
    }

    @Override
    public List<Evento> listarEventosPendientes() {
        return eventoRepository.findByEstado("Pendiente");
    }

    @Override
    @Transactional
    public void aprobarEvento(Integer idEvento) {
        Evento evento = eventoRepository.findById(idEvento)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        evento.setEstado("Activo");
        eventoRepository.save(evento);

        Usuario dueno = evento.getOrganizador();
        if (dueno != null && dueno.getRol() != null) {
            String rolActual = dueno.getRol().getNombreRol();
            if (!rolActual.equalsIgnoreCase("Administrador") &&
                    !rolActual.equalsIgnoreCase("Organizador") &&
                    !rolActual.equalsIgnoreCase("Personal")) {

                Rol rolOrganizador = rolRepository.findByNombreRol("Organizador")
                        .orElseThrow(() -> new RuntimeException("Rol Organizador no encontrado"));

                dueno.setRol(rolOrganizador);
                usuarioRepository.save(dueno);
            }
        }
    }

    @Override
    public void rechazarEvento(Integer idEvento) {
        Evento evento = eventoRepository.findById(idEvento)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        evento.setEstado("Rechazado");
        eventoRepository.save(evento);
    }

    @Override
    public List<Evento> listarPorOrganizador(Integer idUsuario) {
        return eventoRepository.findByOrganizador_IdUsuario(idUsuario);
    }

}
