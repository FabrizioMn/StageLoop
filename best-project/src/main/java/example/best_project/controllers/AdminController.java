package example.best_project.controllers;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import example.best_project.model.Categoria;
import example.best_project.model.Evento;
import example.best_project.model.Orden;
import example.best_project.model.Usuario;
import example.best_project.repository.OrdenRepository;
import example.best_project.repository.PagoRepository;
import example.best_project.repository.UsuarioRepository;
import example.best_project.services.CategoriaService;
import example.best_project.services.EventoService;
import example.best_project.services.UsuarioService;
import example.best_project.services.VentaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class AdminController {

    @Autowired
    private EventoService eventoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private VentaService ventaService;

    @GetMapping("/inicio")
    public String home(Model model, HttpServletRequest request) {
        model.addAttribute("title", "Inicio - Mi app eventos");
        model.addAttribute("content", "admin/inicio");
        model.addAttribute("activePage", "/inicio");
        return "layouts/base-layout";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // KPI: INGRESOS TOTALES (Suma de la tabla Pago)
        BigDecimal ingresos = pagoRepository.sumarIngresosTotales();
        model.addAttribute("totalIngresos", ingresos != null ? ingresos : BigDecimal.ZERO);

        // KPI: EVENTOS Y SOLICITUDES
        model.addAttribute("totalEventos", eventoService.listarEventos().size());
        model.addAttribute("solicitudesPendientes", eventoService.listarEventosPendientes().size());

        // KPI: USUARIOS (Contamos total en la BD)
        model.addAttribute("totalUsuarios", usuarioRepository.count());

        // TABLA: ÚLTIMAS 5 VENTAS
        model.addAttribute("ultimasOrdenes", ordenRepository.findTop5ByOrderByFechaOrdenDesc());

        // Configuración de vista
        model.addAttribute("title", "Dashboard - Admin Panel");
        model.addAttribute("content", "admin/dashboard");
        model.addAttribute("activePage", "/dashboard");

        return "layouts/base-layout";
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        List<Orden> todasLasOrdenes = ordenRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaOrden"));

        model.addAttribute("ordenes", todasLasOrdenes);
        model.addAttribute("title", "Gestión de Órdenes");
        model.addAttribute("content", "admin/orders");
        model.addAttribute("activePage", "/orders");
        return "layouts/base-layout";
    }

    // ================
    // EVENTOS
    // ================
    @GetMapping("/events")
    public String events(Model model) {
        model.addAttribute("title", "Eventos - Mi app eventos");
        model.addAttribute("content", "admin/events");
        model.addAttribute("activePage", "/events");
        model.addAttribute("eventos", eventoService.listarEventos());
        model.addAttribute("evento", new Evento());
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "layouts/base-layout";
    }

    @GetMapping("/events/delete/{id}")
    public String deleteEvent(@PathVariable Integer id) {
        eventoService.eliminarEvento(id);
        return "redirect:/events";
    }

    @PostMapping("/events/save")
    public String saveEvent(@Valid @ModelAttribute Evento evento,
            BindingResult result,
            @RequestParam("archivoImagen") MultipartFile file) {

        if (result.hasErrors()) {
            String msg = result.getFieldError().getDefaultMessage();
            return "redirect:/events?error=" + msg;
        }

        try {
            Usuario admin = new Usuario();
            admin.setIdUsuario(1);
            evento.setOrganizador(admin);
            evento.setEstado("Activo");

            if (!file.isEmpty()) {
                evento.setImagen(file.getBytes());
            }

            eventoService.guardarEvento(evento);
            return "redirect:/events?created";

        } catch (Exception e) {
            return "redirect:/events?error=Error al guardar: " + e.getMessage();
        }
    }

    @PostMapping("/events/update")
    public String updateEvent(@Valid @ModelAttribute Evento evento,
            BindingResult result,
            @RequestParam("idEvento") Integer id,
            @RequestParam("categoria.idCategoria") Integer idCategoria,
            @RequestParam(value = "archivoImagen", required = false) MultipartFile file) {

        if (result.hasErrors()) {
            return "redirect:/events?error=" + result.getFieldError().getDefaultMessage();
        }

        try {
            Categoria cat = new Categoria();
            cat.setIdCategoria(idCategoria);
            evento.setCategoria(cat);
            eventoService.actualizarEvento(id, evento, file);
            return "redirect:/events?updated";
        } catch (Exception e) {
            return "redirect:/events?error=" + e.getMessage();
        }
    }

    // ================
    // ORGANIZADOR
    // ================
    @GetMapping("/organizers")
    public String organizers(Model model) {
        model.addAttribute("organizadores", usuarioService.listarOrganizadores());
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("title", "Organizadores - Mi App");
        model.addAttribute("content", "admin/organizers");
        model.addAttribute("activePage", "/organizers");

        return "layouts/base-layout";
    }

    @PostMapping("/organizers/save")
    public String saveOrganizer(@Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            String mensajeError = result.getFieldError().getDefaultMessage();

            return "redirect:/organizers?error=" + mensajeError;
        }

        try {

            usuarioService.guardarOrganizador(usuario);
            return "redirect:/organizers?created";

        } catch (Exception e) {

            return "redirect:/organizers?error=" + e.getMessage();
        }
    }

    @GetMapping("/organizers/delete/{id}")
    public String deleteOrganizer(@PathVariable Integer id) {
        usuarioService.eliminarUsuario(id);
        return "redirect:/organizers";
    }

    @PostMapping("/organizers/update")
    public String updateOrganizer(@ModelAttribute Usuario usuario) {
        try {
            // El objeto 'usuario' ya trae el idUsuario y los datos del formulario
            usuarioService.actualizarOrganizador(usuario.getIdUsuario(), usuario);
            return "redirect:/organizers?updated";
        } catch (Exception e) {
            return "redirect:/organizers?error=" + e.getMessage();
        }
    }

    // ================
    // EMPLEADO VISTAS
    // ================

    @GetMapping("/requests")
    public String reviewRequests(Model model) {
        model.addAttribute("solicitudes", eventoService.listarEventosPendientes());
        model.addAttribute("title", "Revisión de Solicitudes");
        model.addAttribute("content", "admin/requests");
        model.addAttribute("activePage", "/requests");
        return "layouts/base-layout";
    }

    @GetMapping("/requests/approve/{id}")
    public String approveEvent(@PathVariable Integer id) {
        eventoService.aprobarEvento(id);
        return "redirect:/requests?approved";
    }

    @GetMapping("/requests/reject/{id}")
    public String rejectEvent(@PathVariable Integer id) {
        eventoService.rechazarEvento(id);
        return "redirect:/requests?rejected";
    }

    // ================
    // ORDENES
    // ================

    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable Integer id) {

        try {
            ventaService.eliminarOrden(id);
            return "redirect:/orders";
        } catch (Exception e) {
            return "redirect:/orders?error=" + e.getMessage();
        }
    }

}
