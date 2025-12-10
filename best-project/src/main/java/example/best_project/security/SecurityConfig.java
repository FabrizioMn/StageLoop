package example.best_project.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter; 

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Desactivamos CSRF para facilitar las pruebas de API en Postman
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()

                //RECURSOS ESTÁTICOS (CSS, JS, Imágenes)
                .requestMatchers("/css/**", "/js/**", "/img/**", "/images/**", "/imgs/**","/imagen/**", "/logo.ico").permitAll()

                // PÁGINAS WEB PÚBLICAS
                .requestMatchers("/", "/explorar", "/nosotros", "/formularioEvento").permitAll()
                .requestMatchers("/login", "/register", "/register/save").permitAll()

                //ZONA ADMIN Y PERSONAL (Seguridad por Roles)
                .requestMatchers("/dashboard", "/events/**", "/organizers/**", "/customers/**", "/requests/**", "/orders/**")
                    .hasAnyRole("Administrador", "Personal")

                .anyRequest().authenticated()
            )

            // CONFIGURACIÓN DE LOGIN WEB (Thymeleaf)
            .formLogin(login -> login
                .loginPage("/login")            
                .defaultSuccessUrl("/", true)   
                .failureUrl("/login?error=true")
                .permitAll()
            )

            // CONFIGURACIÓN DE LOGOUT WEB
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )

            // VINCULAR PROVEEDOR
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}