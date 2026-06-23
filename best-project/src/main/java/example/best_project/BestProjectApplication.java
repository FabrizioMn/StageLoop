package example.best_project;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import example.best_project.user.domain.Role;
import example.best_project.user.repository.RoleRepository;

@SpringBootApplication
public class BestProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(BestProjectApplication.class, args);
	}

	@Bean
	CommandLineRunner initRoles(RoleRepository roleRepository) {
		return args -> {
			// Inicializar Roles Globales
			if (roleRepository.count() == 0) {
				Role organizador = new Role(null, "Organizador");
				Role cliente = new Role(null, "Cliente");
				roleRepository.saveAll(List.of(organizador, cliente));
				System.out.println(">>Roles iniciales insertados");
			}
		};
	}

}
