package com.example.projetoptimizer.auth;

import com.example.projetoptimizer.auth.models.GithubUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class GithubTokenAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(GithubTokenAuthenticationFilter.class);

    public static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_TYPE = "Bearer ";
    private static final String GITHUB_USER_URI = "https://api.github.com/user";

    private final RestClient restClient = RestClient.create();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String githubTokenWithTokenType = request.getHeader(TOKEN_HEADER);

        if (githubTokenWithTokenType == null || !githubTokenWithTokenType.startsWith(TOKEN_TYPE)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token d'authentification manquant ou invalide");
            return;
        }

        var githubToken = githubTokenWithTokenType.substring(TOKEN_TYPE.length());

        try {
            var githubUser = restClient
                    .get()
                    .uri(GITHUB_USER_URI)
                    .header(TOKEN_HEADER, githubTokenWithTokenType)
                    .retrieve()
                    .body(GithubUser.class);

            if (githubUser == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Impossible de récupérer les informations de l'utilisateur GitHub");
                return;
            }

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(githubUser,
                    githubToken, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Erreur d'authentification: " + ex.getMessage());
        }
    }
}
