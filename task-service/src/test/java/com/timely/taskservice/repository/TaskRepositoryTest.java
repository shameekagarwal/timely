package com.timely.taskservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import com.timely.taskservice.constant.TaskStatus;
import com.timely.taskservice.model.Project;
import com.timely.taskservice.model.Task;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

@DataMongoTest
public class TaskRepositoryTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TaskRepository taskRepository;

    private List<Project> projects;

    private Project saveProject() {
        Project project = new Project();
        return mongoTemplate.save(project);
    }

    private Task saveTask(Project project) {
        Task task = new Task();
        task.setTitle(RandomStringUtils.randomAlphabetic(10));
        task.setTaskStatus(TaskStatus.DONE);
        task.setProject(project);
        return mongoTemplate.save(task);
    }

    // project 1 - task 1, task 2
    // project 2 - task 3

    @BeforeEach
    public void setup() {
        Project projectOne = saveProject();
        Project projectTwo = saveProject();
        saveTask(projectOne);
        saveTask(projectOne);
        saveTask(projectTwo);

        projects = List.of(projectOne, projectTwo);
    }

    @Test
    public void whenFindByProjectId_thenSuccess() {
        List<Task> tasks;

        tasks = taskRepository.findByProjectId(projects.get(0).getId());
        assertEquals(2, tasks.size());
        tasks = taskRepository.findByProjectId(projects.get(1).getId());
        assertEquals(1, tasks.size());
    }

}
