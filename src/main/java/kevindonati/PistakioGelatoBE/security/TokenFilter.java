package kevindonati.PistakioGelatoBE.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kevindonati.PistakioGelatoBE.entities.User;
import kevindonati.PistakioGelatoBE.exceptions.UnauthorizedException;
import kevindonati.PistakioGelatoBE.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class TokenFilter extends OncePerRequestFilter {
    @Autowired
    private JWTTools jwtTools;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String accessToken = authorizationHeader.replace("Bearer ", "");
            this.jwtTools.verificoToken(accessToken);

            UUID userId = this.jwtTools.checkIdDalToken(accessToken);
            User autenticato = this.userRepository.findById(userId)
                    .orElseThrow(() ->
                            new UnauthorizedException("User not found")
                    );

            if (!autenticato.isEnabled()) {
                throw new UnauthorizedException("User is disabled");
            }

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            autenticato,
                            null,
                            List.of(
                                    new SimpleGrantedAuthority(
                                            "ROLE_" + autenticato.getRole().name()
                                    )
                            )
                    );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter()
                    .write(
                            "{\"error\": \"Token non valido\", \"message\": \"" +
                                    e.getMessage() + "\"}"
                    );
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return new AntPathMatcher().match("/auth/**", request.getServletPath());
    }
}
