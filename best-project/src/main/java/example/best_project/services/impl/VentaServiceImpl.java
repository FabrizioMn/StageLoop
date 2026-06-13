package example.best_project.services.impl;

import java.math.BigDecimal;
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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {

    private final EventoRepository eventoRepository;
    private final OrdenRepository ordenRepository;
    private final DetalleOrdenRepository detalleOrdenRepository;
    private final PagoRepository pagoRepository;

    @Override
    @Transactional
    public void procesarCompra(Usuario comprador,@NonNull Integer idEvento, Integer cantidad) {

        if (cantidad == null || cantidad <= 0) {
            throw new RuntimeException("La cantidad de entradas debe ser mayor a cero");
        }

        Evento evento = eventoRepository.findById(idEvento)
                .orElseThrow(() -> new RuntimeException("El evento no existe"));

        if (evento.getCapacidad() < cantidad) {
            throw new RuntimeException("Lo sentimos, solo quedan " + evento.getCapacidad() + " entradas.");
        }

        Orden orden = new Orden();
        orden.setUsuario(comprador);
        orden = ordenRepository.save(orden);

        DetalleOrden detalle = new DetalleOrden();
        detalle.setOrden(orden);
        detalle.setEvento(evento);
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(evento.getPrecio());
        detalleOrdenRepository.save(detalle);

        evento.setCapacidad(evento.getCapacidad() - cantidad);
        eventoRepository.save(evento);

        // Registrar pago
        BigDecimal totalPagar = evento.getPrecio().multiply(BigDecimal.valueOf(cantidad));

        Pago pago = new Pago();
        pago.setMonto(totalPagar);
        pago.setMetodoPago("Tajeta de Credito");
        pago.setOrden(orden);
        pagoRepository.save(pago);
    }

    @Override
    @Transactional
    public void eliminarOrden(@NonNull Integer idOrden) {
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new RuntimeException("La orden no existe"));

        if (orden.getDetalles() != null) {
            for (DetalleOrden detalle : orden.getDetalles()) {
                Evento evento = detalle.getEvento();
                if (evento != null) {
                    evento.setCapacidad(evento.getCapacidad() + detalle.getCantidad());
                    eventoRepository.save(evento);
                }
            }
        }

        pagoRepository.deleteByOrden_IdOrden(idOrden);
        ordenRepository.deleteById(idOrden);
    }

}
