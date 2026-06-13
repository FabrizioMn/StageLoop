package example.best_project.user;

public record UserResponseDto(
        Integer userId,
        String name,
        String email,
        String roleName) {
}
