package com.example.projetoptimizer.user;


import com.example.projetoptimizer.auth.models.GithubUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("current-user")
    public GithubUser getCurrentUser() {
        return userService.getCurrentUser();
    }

}
