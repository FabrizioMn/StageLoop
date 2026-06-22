package example.best_project.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import example.best_project.auth.domain.Token;
import example.best_project.auth.domain.Token.TokenType;
import example.best_project.auth.dto.LoginRequest;
import example.best_project.auth.dto.RegisterRequest;
import example.best_project.auth.dto.TokenResponse;
import example.best_project.auth.repository.TokenRepository;
import example.best_project.user.domain.User;
import example.best_project.user.repository.RoleRepository;
import example.best_project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UserRepository userRepository;
        private final TokenRepository tokenRepository;
        private final PasswordEncoder passwordEncoder;
        private final RoleRepository roleRepository;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;

        public TokenResponse register(RegisterRequest request) {

                var rolCliente = roleRepository.findByRoleName("Cliente")
                                .orElseThrow(() -> new RuntimeException("El rol Cliente no exite"));

                var user = User.builder()
                                .name(request.name())
                                .email(request.email())
                                .password(passwordEncoder.encode(request.password()))
                                .role(rolCliente)
                                .build();

                var savedUser = userRepository.save(user);

                UserDetails userDetails = org.springframework.security.core.userdetails.User
                                .withUsername(savedUser.getEmail())
                                .password(savedUser.getPassword())
                                .authorities(savedUser.getRole().getRoleName())
                                .build();

                var jwtToken = jwtService.generateToken(userDetails);
                var refreshToken = jwtService.generateRefreshToken(userDetails);
                saveUserToken(savedUser, jwtToken);
                return new TokenResponse(jwtToken, refreshToken);
        }

        public TokenResponse login(LoginRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

                var user = userRepository.findByEmail(request.email())
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                UserDetails userDetails = org.springframework.security.core.userdetails.User
                                .withUsername(user.getEmail())
                                .password(user.getPassword())
                                .authorities(user.getRole().getRoleName())
                                .build();

                var jwtToken = jwtService.generateToken(userDetails);
                var refreshToken = jwtService.generateRefreshToken(userDetails);
                saveUserToken(user, jwtToken);
                return new TokenResponse(jwtToken, refreshToken);

        }

        private void saveUserToken(User user, String jwtToken) {
                var token = Token.builder()
                                .user(user)
                                .token(jwtToken)
                                .tokenType(TokenType.BEARER)
                                .expired(false)
                                .revoked(false)
                                .build();
                tokenRepository.save(token);
        }

}
