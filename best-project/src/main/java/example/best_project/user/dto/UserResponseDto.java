package example.best_project.user.dto;

public record UserResponseDto(
        Integer userId,
        String name,
        String email,
        String roleName) {
}
