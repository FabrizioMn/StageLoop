package example.best_project.services.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import example.best_project.model.DetalleOrden;
import example.best_project.model.Evento;
import example.best_project.model.Orden;
import example.best_project.model.Pago;
import example.best_project.model.Usuario;
import example.best_project.repository.DetalleOrdenRepository;
import example.best_project.repository.EventoRepository;
import example.best_project.repository.OrdenRepository;
import example.best_project.repository.PagoRepository;
import example.best_project.services.VentaService;
import jakarta.transaction.Transactional;

@Service
public class VentaServiceImpl implements VentaService {

    @Autowired
    private EventoRepository eventoRepository;
    @Autowired
    private OrdenRepository ordenRepository;
    @Autowired
    private DetalleOrdenRepository detalleOrdenRepository;
    @Autowired
    private PagoRepository pagoRepository;

    @Override
    @Transactional
    public void procesarCompra(Usuario comprador, Integer idEvento, Integer cantidad) {

        Evento evento = eventoRepository.findById(idEvento)
                .orElseThrow(() -> new RuntimeException("El evento no existe"));

        if (evento.getCapacidad() < cantidad) {
            throw new RuntimeException("Lo sentimos, solo quedan " + evento.getCapacidad() + " entradas.");
        }

        Orden orden = new Orden();
        orden.setUsuario(comprador);
        orden.setFechaOrden(LocalDateTime.now());
        orden = ordenRepository.save(orden);

        DetalleOrden detalle = new DetalleOrden();
        detalle.setOrden(orden);
        detalle.setEvento(evento);
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(evento.getPrecio());
        detalleOrdenRepository.save(detalle);

        evento.setCapacidad(evento.getCapacidad() - cantidad);
        eventoRepository.save(evento);

        // registrar pago
        BigDecimal totalPagar = evento.getPrecio().multiply(new BigDecimal(cantidad));

        Pago pago = new Pago();
        pago.setMonto(totalPagar);
        pago.setFechaPago(LocalDateTime.now());
        pago.setMetodoPago("Tajeta de Credito");
        pago.setOrden(orden);
        pagoRepository.save(pago);
    }

    @Override
    @Transactional
    public void eliminarOrden(Integer idOrden) {
        pagoRepository.deleteByOrden_IdOrden(idOrden);
        ordenRepository.deleteById(idOrden);
    }

}
