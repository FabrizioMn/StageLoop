package example.best_project.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import example.best_project.model.Rol;
import example.best_project.model.Usuario;
import example.best_project.repository.RolRepository;
import example.best_project.repository.UsuarioRepository;
import example.best_project.services.UsuarioService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    // USUARIOS
    @Override
    public Usuario registrarCliente(Usuario usuario) {

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya esta registrado");
        }

        // Asignar rol cliente por defecto o se busca por nombre para ser mas seguro
        Rol rolCliente = rolRepository.findByNombreRol("Cliente")
                .orElseThrow(() -> new RuntimeException("Error: Rol 'Cliente' no existe encontrado en la BD "));

        usuario.setRol(rolCliente);

        String passwordEncriptada = passwordEncoder.encode(usuario.getContrasena());
        usuario.setContrasena(passwordEncriptada);

        return usuarioRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    public boolean existeEmail(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }

    @Override
    public Usuario obtenerPorId(@NonNull Integer id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @Override
    public void eliminarUsuario(@NonNull Integer id) {
        usuarioRepository.deleteById(id);
    }

    // ORGANIZADORES
    @Override
    public List<Usuario> listarOrganizadores() {
        return usuarioRepository.findByRol_NombreRol("Organizador");
    }

    @Override
    public Usuario guardarOrganizador(Usuario usuario) {
        Rol rolOrganizador = rolRepository.findByNombreRol("Organizador")
                .orElseThrow(() -> new RuntimeException("Error: Rol 'Organizador' no existe"));
        usuario.setRol(rolOrganizador);

        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario actualizarOrganizador(@NonNull Integer id, Usuario usuarioDetalles) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuarioExistente.setNombre(usuarioDetalles.getNombre());
        usuarioExistente.setEmail(usuarioDetalles.getEmail());
        usuarioExistente.setNumTelefono(usuarioDetalles.getNumTelefono());

        if (usuarioDetalles.getContrasena() != null && !usuarioDetalles.getContrasena().isEmpty()) {
            String passEncriptada = passwordEncoder.encode(usuarioDetalles.getContrasena());
            usuarioExistente.setContrasena(passEncriptada);
        }

        return usuarioRepository.save(usuarioExistente);
    }

}
