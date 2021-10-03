package com.timely.taskservice.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.google.firebase.auth.FirebaseAuth;
import com.timely.taskservice.filter.FirebaseFilter;
import com.timely.taskservice.model.Project;
import com.timely.taskservice.model.Task;
import com.timely.taskservice.model.User;
import com.timely.taskservice.repository.ProjectRepository;
import com.timely.taskservice.repository.TaskRepository;
import com.timely.taskservice.util.TrackExecutionTime;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TaskController {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final FirebaseAuth firebaseAuth;

    @GetMapping("ping")
    @TrackExecutionTime
    public String ping() {
        return "pong from task-service";
    }

    @RabbitListener(queues = "${custom.rabbitmq.project-save-queue}")
    @TrackExecutionTime
    public void projectSaveListener(Project project) {
        projectRepository.save(project);
    }

    @RabbitListener(queues = "${custom.rabbitmq.project-delete-queue}")
    @TrackExecutionTime
    public void projectDeleteListener(String projectId) {
        projectRepository.deleteById(projectId);
	List<Task> tasks = taskRepository.findByProjectId(projectId);
	tasks.forEach(taskRepository::delete);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('" + User.ASSOCIATE_ROLE + "', '" + User.MANAGER_ROLE + "')")
    @TrackExecutionTime
    public List<Task> getTasks(HttpServletRequest request) throws Exception {
        String userId = FirebaseFilter.extractUserRecordFromRequest(request, firebaseAuth).getUid();
        List<Project> projects = projectRepository.findByAssociatesIdsContainingOrManagerId(userId, userId);
        List<Task> tasks = new ArrayList<>();
        projects.forEach(project -> tasks.addAll(taskRepository.findByProjectId(project.getId())));
        return tasks;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('" + User.ASSOCIATE_ROLE + "')")
    @TrackExecutionTime
    public Task createTask(@RequestBody @Valid Task task, HttpServletRequest request) throws Exception {
        String associateId = FirebaseFilter.extractUserRecordFromRequest(request, firebaseAuth).getUid();
        Project project = projectRepository.findByIdAndAssociatesIdsContaining(task.getProject().getId(), associateId).get();
        task.setProject(project);
        return taskRepository.save(task);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('" + User.ASSOCIATE_ROLE + "')")
    @TrackExecutionTime
    public Task updateTask(@RequestBody @Valid Task task, HttpServletRequest request) throws Exception {
        String associateId = FirebaseFilter.extractUserRecordFromRequest(request, firebaseAuth).getUid();
        Project project = projectRepository.findByIdAndAssociatesIdsContaining(task.getProject().getId(), associateId).get();
        taskRepository.findById(task.getId()).get();
        task.setProject(project);
        return taskRepository.save(task);
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('" + User.ASSOCIATE_ROLE + "')")
    @TrackExecutionTime
    public String deleteTask(@RequestParam String taskId, HttpServletRequest request) throws Exception {
        String associateId = FirebaseFilter.extractUserRecordFromRequest(request, firebaseAuth).getUid();
        Task task = taskRepository.findById(taskId).get();
        Project project = projectRepository.findByIdAndAssociatesIdsContaining(task.getProject().getId(), associateId).get();
        task.setProject(project);
        taskRepository.delete(task);
        return "task with id %s deleted successfully".formatted(taskId);
    }

}
