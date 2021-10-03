package com.timely.projectservice.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.google.firebase.auth.FirebaseAuth;
import com.timely.projectservice.filter.FirebaseFilter;
import com.timely.projectservice.model.Project;
import com.timely.projectservice.model.User;
import com.timely.projectservice.repository.ProjectRepository;
import com.timely.projectservice.util.TrackExecutionTime;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final FirebaseAuth firebaseAuth;
    private final RabbitTemplate rabbitTemplate;
    private final String projectSaveBindingKey;
    private final String projectDeleteBindingKey;
    private final String exchangeName;

    public ProjectController(ProjectRepository projectRepository, FirebaseAuth firebaseAuth,
            RabbitTemplate rabbitTemplate,
            @Value("${custom.rabbitmq.project-save-binding}") String projectSaveBindingKey,
            @Value("${custom.rabbitmq.project-delete-binding}") String projectDeleteBindingKey,
            @Value("${custom.rabbitmq.exchange}") String exchangeName) {
        this.projectRepository = projectRepository;
        this.firebaseAuth = firebaseAuth;
        this.rabbitTemplate = rabbitTemplate;
        this.projectSaveBindingKey = projectSaveBindingKey;
        this.projectDeleteBindingKey = projectDeleteBindingKey;
        this.exchangeName = exchangeName;
    }

    @GetMapping("ping")
    @TrackExecutionTime
    public String ping() {
        return "pong from project-service";
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('" + User.ASSOCIATE_ROLE + "', '" + User.MANAGER_ROLE + "')")
    @TrackExecutionTime
    public List<Project> getProjects(HttpServletRequest request) throws Exception {
        String userId = FirebaseFilter.extractUserRecordFromRequest(request, firebaseAuth).getUid();
        return projectRepository.findByAssociatesIdsContainingOrManagerId(userId, userId);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('" + User.MANAGER_ROLE + "')")
    @TrackExecutionTime
    public Project createProject(@RequestBody @Valid Project project, HttpServletRequest request) throws Exception {
        String managerId = FirebaseFilter.extractUserRecordFromRequest(request, firebaseAuth).getUid();
        project.setManagerId(managerId);
        Project savedProject = projectRepository.save(project);
        rabbitTemplate.convertAndSend(exchangeName, projectSaveBindingKey, savedProject);
        return savedProject;
    }

    @PutMapping
    @PreAuthorize("hasAuthority('" + User.MANAGER_ROLE + "')")
    @TrackExecutionTime
    public Project updateProject(@RequestBody @Valid Project project, HttpServletRequest request) throws Exception {
        String managerId = FirebaseFilter.extractUserRecordFromRequest(request, firebaseAuth).getUid();
        project.setManagerId(managerId);
        projectRepository.findByIdAndManagerId(project.getId(), managerId).get();
        Project savedProject = projectRepository.save(project);
        rabbitTemplate.convertAndSend(exchangeName, projectSaveBindingKey, savedProject);
        return savedProject;
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('" + User.MANAGER_ROLE + "')")
    @TrackExecutionTime
    public String deleteProject(@RequestParam String projectId, HttpServletRequest request) throws Exception {
        String managerId = FirebaseFilter.extractUserRecordFromRequest(request, firebaseAuth).getUid();
        projectRepository.findByIdAndManagerId(projectId, managerId).get();
        projectRepository.deleteById(projectId);
        rabbitTemplate.convertAndSend(exchangeName, projectDeleteBindingKey, projectId);
        return "project with id %s deleted successfully".formatted(projectId);
    }

}
