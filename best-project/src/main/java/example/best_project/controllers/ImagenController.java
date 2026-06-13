package example.best_project.controllers;

import example.best_project.model.Evento;
import example.best_project.services.EventoService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/imagen")
@RequiredArgsConstructor
public class ImagenController {

    private final EventoService eventoService;

    @GetMapping("/ver/{id}")
    public ResponseEntity<byte[]> mostrarImagen(@PathVariable Integer id) {
        Evento evento = eventoService.obtenerEventoPorId(id);

        if (evento == null || evento.getImagen() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"imagen.jpg\"")
                .contentType(MediaType.IMAGE_JPEG)
                .body(evento.getImagen());
    }
}