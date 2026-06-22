package example.best_project.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import example.best_project.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
