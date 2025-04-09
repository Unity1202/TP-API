package com.example.projetoptimizer.project.controller;

import com.example.projetoptimizer.project.dtos.Project;
import com.example.projetoptimizer.project.dtos.ProjetCreationInput;
import com.example.projetoptimizer.project.service.ProjectService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projets")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<Project> getProjects() {
        return projectService.getProjects();
    }

    @PostMapping
    public Project createProject(@RequestBody ProjetCreationInput input) {
        return projectService.createProject(input.name(), input.description());
    }

    @PutMapping("/add-user/{projectName}")
    public void addUserToProject(
            @PathVariable String projectName,
            @RequestParam String user) {
        projectService.inviteUsersToProject(projectName, List.of(user));
    }

    @DeleteMapping("/{projectName}")
    public void deleteProject(@PathVariable String projectName) {
        projectService.deleteProject(projectName);
    }

    @PostMapping("/initiate/{projectName}")
    public void initiateProject(
            @PathVariable String projectName,
            @RequestParam List<String> user) {
        projectService.initiateProject(projectName, user);
    }
}