package com.example.projetoptimizer.project.service;

import com.example.projetoptimizer.project.dtos.Project;
import com.example.projetoptimizer.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;

@Service
public class ProjectService {

    private final RestClient restClient = RestClient.create();
    private final static String GITHUB_USER_REPO_ENDPOINT = "https://api.github.com/user/repos";
    private final static String GITHUB_INVITE_USER_TO_REPO_ENDPOINT = "https://api.github.com/repos/{owner}/{repo}/collaborators/{username}";
    private final static String GITHUB_DELETE_REPO_ENDPOINT = "https://api.github.com/repos/{owner}/{repo}";

    private final UserService userService;

    @Autowired
    public ProjectService(UserService userService) {
        this.userService = userService;
    }

    public List<Project> getProjects() {
        var token = userService.getCurrentUserToken();

        Project[] githubProjects = restClient
                .get()
                .uri(GITHUB_USER_REPO_ENDPOINT)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(Project[].class);

        if (githubProjects == null) {
            throw new RuntimeException("");
        }

        return Arrays.stream(githubProjects).toList();
    }

    public Project createProject(String name, String description) {
        var token = userService.getCurrentUserToken();

        Project githubProject = restClient
                .post()
                .uri(GITHUB_USER_REPO_ENDPOINT)
                .header("Authorization", "Bearer " + token)
                .body(new Project(name, description))
                .retrieve()
                .body(Project.class);

        if (githubProject == null) {
            throw new RuntimeException("Impossible de créer le projet");
        }

        return githubProject;
    }

    public void inviteUsersToProject(String projectName, List<String> usernames) {
        var token = userService.getCurrentUserToken();
        var owner = userService.getCurrentUsername();

        usernames.forEach(username -> {
            restClient
                    .put()
                    .uri(GITHUB_INVITE_USER_TO_REPO_ENDPOINT, owner, projectName, username)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(Project.class);

        });

    }

    public void initiateProject(String projectName, List<String> usersEmail) {
        var token = userService.getCurrentUserToken();
        var owner = userService.getCurrentUsername();

        Project githubProject = restClient
                .post()
                .uri(GITHUB_USER_REPO_ENDPOINT)
                .header("Authorization", "Bearer " + token)
                .body(new Project(projectName, ""))
                .retrieve()
                .body(Project.class);

        if (githubProject == null) {
            throw new RuntimeException("Impossible de créer le projet");
        }

        usersEmail.forEach(username -> {
            restClient
                    .put()
                    .uri(GITHUB_INVITE_USER_TO_REPO_ENDPOINT, owner, projectName, username)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(Project.class);
        });
    }

    public void deleteProject(String projectName) {
        var token = userService.getCurrentUserToken();
        var owner = userService.getCurrentUsername();

        restClient
                .delete()
                .uri(GITHUB_DELETE_REPO_ENDPOINT, owner, projectName)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toBodilessEntity();
    }

}
