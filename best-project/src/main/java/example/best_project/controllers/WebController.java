package example.best_project.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import example.best_project.model.Evento;
import example.best_project.model.Orden;
import example.best_project.model.Usuario;
import example.best_project.repository.OrdenRepository;
import example.best_project.services.CategoriaService;
import example.best_project.services.EventoService;
import example.best_project.services.UsuarioService;
import example.best_project.services.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final EventoService eventoService;
    private final CategoriaService categoriaService;
    private final UsuarioService usuarioService;
    private final VentaService ventaService;
    private final OrdenRepository ordenRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("eventos", eventoService.listarEventosActivos());
        return "web/inicio";
    }

    @GetMapping("/explorar")
    public String explorar(Model model) {
        model.addAttribute("eventos", eventoService.listarEventosActivos());
        return "web/explorar";
    }

    @GetMapping("/nosotros")
    public String nosotros(Model model) {
        return "web/nosotros";
    }

    // ==============
    // Crear Eventos
    // ==============

    @GetMapping("/solicitar-evento")
    public String mostrarFormulario(Model model) {
        model.addAttribute("evento", new Evento());
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "web/solicitar-evento";
    }

    @PostMapping("/solicitar-evento")
    public String procesarSolicitud(@Valid @ModelAttribute Evento evento,
            BindingResult result,
            @RequestParam("archivoImagen") MultipartFile archivo,
            Model model,
            Principal principal) {

        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.listarTodas());
            return "web/solicitar-evento";
        }

        try {
            String emailUsuario = principal.getName();
            Usuario usuarioLogueado = usuarioService.buscarPorEmail(emailUsuario).get();
            evento.setEstado("Pendiente");
            evento.setOrganizador(usuarioLogueado);

            // LÓGICA DE LA IMAGEN
            if (!archivo.isEmpty()) {
                evento.setImagen(archivo.getBytes());
            }

            eventoService.guardarEvento(evento);
            return "redirect:/?exitoSolicitud";

        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            return "web/solicitar-evento";
        }
    }

    // ==============
    // Ver eventos de organizadores
    // ==============
    @GetMapping("/mis-eventos")
    public String misEventos(Model model, Principal principal) {
        String email = principal.getName();
        Usuario organizador = usuarioService.buscarPorEmail(email).get();

        List<Evento> susEventos = eventoService.listarPorOrganizador(organizador.getIdUsuario());

        model.addAttribute("eventos", susEventos);
        model.addAttribute("activePage", "/mis-eventos");
        return "web/mis-eventos";
    }

    // ==============
    // Compra de Entradas
    // ==============

    @GetMapping("/checkout/{id}")
    public String mostrarCheckout(@PathVariable Integer id, Model model) {
        Evento evento = eventoService.obtenerEventoPorId(id);

        // Si no hay stock, no dejar comprar
        if (evento.getCapacidad() <= 0) {
            return "redirect:/explorar?sinStock";
        }

        model.addAttribute("evento", evento);
        return "web/checkout";
    }

    @PostMapping("/checkout/procesar")
    public String procesarCompra(@RequestParam Integer idEvento,
            @RequestParam Integer cantidad,
            Principal principal,
            Model model) {
        try {
            String email = principal.getName();
            Usuario usuario = usuarioService.buscarPorEmail(email).get();

            ventaService.procesarCompra(usuario, idEvento, cantidad);

            return "redirect:/compra-exitosa";

        } catch (Exception e) {
            // Si falla (ej: se acabó el stock justo ahora), volver al checkout con error
            return "redirect:/checkout/" + idEvento + "?error=" + e.getMessage();
        }
    }

    @GetMapping("/compra-exitosa")
    public String exito() {
        return "web/exito";
    }

    // ==============
    // Ver compras
    // ==============

    @GetMapping("/mis-compras")
    public String misCompras(Model model, Principal principal) {
        String email = principal.getName();
        Usuario usuario = usuarioService.buscarPorEmail(email).get();

        // Buscar sus órdenes usando el método del Repository
        List<Orden> misOrdenes = ordenRepository.findByUsuario_IdUsuarioOrderByFechaOrdenDesc(usuario.getIdUsuario());

        model.addAttribute("ordenes", misOrdenes);
        return "web/mis-compras";
    }

}
