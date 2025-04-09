package com.example.projetoptimizer.user;

import com.example.projetoptimizer.auth.models.GithubUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public GithubUser getCurrentUser() {

        return (GithubUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }


    public String getCurrentUsername() {
        return getCurrentUser().login();
    }

    public String getCurrentUserToken() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
    }
}
