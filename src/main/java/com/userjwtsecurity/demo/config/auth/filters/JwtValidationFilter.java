package com.userjwtsecurity.demo.config.auth.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userjwtsecurity.demo.config.auth.utils.SimpleGrantedAuthorityJsonCreator;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import static com.userjwtsecurity.demo.config.auth.utils.TokenjwtConfig.*;

public class JwtValidationFilter extends BasicAuthenticationFilter {

    public JwtValidationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 1. Obtenemos la cabecera de autenticación
        String header = request.getHeader(HEADER_AUTHORIZATION);

        // 2. Validar si el Authorization tiene la palabra bearer con el token.
        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            // Continuamos con la cadena de filtro y no retornamos nada.
            chain.doFilter(request, response);
            return;
        }

        // Si pasa la condición, entonces se valida y obtenemos el token.
        String token = header.replace(TOKEN_PREFIX, "");

        try {

            // Validamos el token
            Claims claims = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();

            Object authoritiesClaims = claims.get("authorities");
            String username = claims.getSubject();

            Collection<? extends GrantedAuthority> authorities = Arrays.asList(
                    new ObjectMapper()
                            .addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreator.class)
                            .readValue(authoritiesClaims.toString().getBytes(), SimpleGrantedAuthority[].class));

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
                    null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            // Continuamos con la cadena de filtro.
            chain.doFilter(request, response);

        } catch (Exception e) {

            Map<String, Object> body = new HashMap<>();
            body.put("error", e.getMessage());
            body.put("message", "Token invalido");

            response.getWriter().write(new ObjectMapper().writeValueAsString(body));

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().flush();
        }

        // super.doFilterInternal(request, response, chain);
    }

}
