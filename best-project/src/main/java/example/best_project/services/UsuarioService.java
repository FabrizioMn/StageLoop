package example.best_project.services;

import java.util.List;
import java.util.Optional;

import example.best_project.model.Usuario;

public interface UsuarioService {
    //Clientes
    Usuario registrarCliente(Usuario usuario);
    Optional<Usuario> buscarPorEmail(String email);
    boolean existeEmail(String email);

    //Organizadores
    List<Usuario> listarOrganizadores();
    Usuario guardarOrganizador(Usuario usuario);
    Usuario actualizarOrganizador(Integer id,Usuario usuarioDetalles);
    Usuario obtenerPorId(Integer id);
    void eliminarUsuario(Integer id);

}
