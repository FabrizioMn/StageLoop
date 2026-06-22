package example.best_project.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import example.best_project.user.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(String roleName);

}
