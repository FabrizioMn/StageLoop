package example.best_project.services;

import example.best_project.model.Usuario;

public interface VentaService {
    void procesarCompra(Usuario comprado, Integer idEvento, Integer cantidad);

    void eliminarOrden(Integer idOrden);

}
