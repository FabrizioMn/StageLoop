package example.best_project.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import example.best_project.event.domain.Categoria;
import example.best_project.repository.CategoriaRepository;
import example.best_project.services.CategoriaService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private CategoriaRepository categoriaRepository;

    @Override
    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

}
