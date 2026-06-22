package example.best_project.auth.dto;

public record LoginRequest(
    String email,
    String password
) {}
