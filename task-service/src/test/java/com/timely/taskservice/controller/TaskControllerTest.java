package com.timely.taskservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.timely.taskservice.constant.TaskStatus;
import com.timely.taskservice.filter.FirebaseFilter;
import com.timely.taskservice.model.Project;
import com.timely.taskservice.model.Task;
import com.timely.taskservice.repository.ProjectRepository;
import com.timely.taskservice.repository.TaskRepository;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

    @MockBean
    private FirebaseAuth firebaseAuth;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    // project 1, project 2
    private List<Project> projects;

    // task 1, task 2, task 3
    private List<Task> tasks;

    // manager 1, associcate 1, associcate 2
    private List<UserRecord> userRecords;

    // project 1 - manager 1, associcate 1
    // project 2 - manager 1, associcate 2
    // project 1 - task 1, task 2
    // project 2 - task 3

    @BeforeEach
    public void setup() {

        List<String> managerIds = List.of(getRandomString(10));
        List<String> associatesIds = List.of(getRandomString(10), getRandomString(10));
        List<String> taskTitles = List.of(getRandomString(10), getRandomString(10), getRandomString(10));
        List<TaskStatus> taskStatuses = List.of(TaskStatus.TODO, TaskStatus.IN_PROGRESS, TaskStatus.DONE);

        projects = List.of(saveProject(managerIds.get(0), Set.of(associatesIds.get(0))),
                saveProject(managerIds.get(0), Set.of(associatesIds.get(1))));
        tasks = List.of(saveTask(taskTitles.get(0), taskStatuses.get(0), projects.get(0)),
                saveTask(taskTitles.get(1), taskStatuses.get(1), projects.get(0)),
                saveTask(taskTitles.get(2), taskStatuses.get(2), projects.get(1)));
        userRecords = List.of(getMockUserRecord(managerIds.get(0)), getMockUserRecord(associatesIds.get(0)),
                getMockUserRecord(associatesIds.get(1)));

        projects.forEach(projectRepository::save);
        tasks.forEach(taskRepository::save);

    }

    @AfterEach
    public void cleanup() {
        projectRepository.deleteAll();
        taskRepository.deleteAll();
    }

    @Test
    public void whenPing_thenSuccess() throws Exception {
        mvc.perform(get("/ping")).andExpect(status().isOk()).andExpect(content().string("pong from task-service"));
    }

    @Test
    public void whenGetTasks_thenSuccess() throws Exception {
        try (MockedStatic<FirebaseFilter> filter = mockStatic(FirebaseFilter.class)) {
            filter.when(() -> FirebaseFilter.extractUserRecordFromRequest(any(), any())).thenReturn(userRecords.get(0));

            mvc.perform(get("/").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(3))
                    .andExpect(jsonPath("$[0].title").value(tasks.get(0).getTitle()))
                    .andExpect(jsonPath("$[1].title").value(tasks.get(1).getTitle()))
                    .andExpect(jsonPath("$[2].title").value(tasks.get(2).getTitle()));
        }

        try (MockedStatic<FirebaseFilter> filter = mockStatic(FirebaseFilter.class)) {
            filter.when(() -> FirebaseFilter.extractUserRecordFromRequest(any(), any())).thenReturn(userRecords.get(1));

            mvc.perform(get("/").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].title").value(tasks.get(0).getTitle()))
                    .andExpect(jsonPath("$[1].title").value(tasks.get(1).getTitle()));
        }

        try (MockedStatic<FirebaseFilter> filter = mockStatic(FirebaseFilter.class)) {
            filter.when(() -> FirebaseFilter.extractUserRecordFromRequest(any(), any())).thenReturn(userRecords.get(2));

            mvc.perform(get("/").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].title").value(tasks.get(2).getTitle()));
        }
    }

    @Test
    public void whenCreateTask_thenSuccess() throws Exception {
        try (MockedStatic<FirebaseFilter> filter = mockStatic(FirebaseFilter.class)) {
            filter.when(() -> FirebaseFilter.extractUserRecordFromRequest(any(), any())).thenReturn(userRecords.get(1));
            Task task = buildTask(getRandomString(10), TaskStatus.IN_PROGRESS, projects.get(0));

            mvc.perform(post("/").contentType(MediaType.APPLICATION_JSON).content(asJsonString(task)))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.title").value(task.getTitle()))
                    .andExpect(jsonPath("$.taskStatus").value(task.getTaskStatus().toString()))
                    .andExpect(jsonPath("$.project.id").value(task.getProject().getId()));
            assertEquals(taskRepository.count(), 4);
        }
    }

    @Test
    public void whenCreateTask_thenFailure() throws Exception {
        try (MockedStatic<FirebaseFilter> filter = mockStatic(FirebaseFilter.class)) {
            filter.when(() -> FirebaseFilter.extractUserRecordFromRequest(any(), any())).thenReturn(userRecords.get(2));
            Task task = buildTask(getRandomString(10), TaskStatus.IN_PROGRESS, projects.get(0));

            mvc.perform(post("/").contentType(MediaType.APPLICATION_JSON).content(asJsonString(task)))
                    .andExpect(status().isInternalServerError());
            assertEquals(taskRepository.count(), 3);
        }
    }

    @Test
    public void whenUpdateTask_thenSuccess() throws Exception {
        try (MockedStatic<FirebaseFilter> filter = mockStatic(FirebaseFilter.class)) {
            filter.when(() -> FirebaseFilter.extractUserRecordFromRequest(any(), any())).thenReturn(userRecords.get(1));
            Task task = tasks.get(0);
            task.setTitle(getRandomString(10));

            mvc.perform(put("/").contentType(MediaType.APPLICATION_JSON).content(asJsonString(task)))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.title").value(task.getTitle()));
            assertEquals(taskRepository.count(), 3);
            Task savedTask = taskRepository.findById(task.getId()).orElseThrow();
            assertEquals(task.getTitle(), savedTask.getTitle());
        }
    }

    @Test
    public void whenUpdateTask_thenFailure() throws Exception {
        try (MockedStatic<FirebaseFilter> filter = mockStatic(FirebaseFilter.class)) {
            filter.when(() -> FirebaseFilter.extractUserRecordFromRequest(any(), any())).thenReturn(userRecords.get(2));
            String title = getRandomString(10);
            Task task = tasks.get(0);
            task.setTitle(title);

            mvc.perform(put("/").contentType(MediaType.APPLICATION_JSON).content(asJsonString(task)))
                    .andExpect(status().isInternalServerError());
            assertEquals(taskRepository.count(), 3);
            Task savedTask = taskRepository.findById(task.getId()).orElseThrow();
            assertNotEquals(task.getTitle(), savedTask.getTitle());
        }
    }

    @Test
    public void whenDeleteTask_thenSuccess() throws Exception {
        try (MockedStatic<FirebaseFilter> filter = mockStatic(FirebaseFilter.class)) {
            filter.when(() -> FirebaseFilter.extractUserRecordFromRequest(any(), any())).thenReturn(userRecords.get(1));
            Task task = tasks.get(0);

            mvc.perform(delete("/").queryParam("taskId", task.getId())).andExpect(status().isOk())
                    .andExpect(content().string("task with id %s deleted successfully".formatted(task.getId())));
            assertEquals(taskRepository.count(), 2);
        }
    }

    @Test
    public void whenDeleteTask_thenFailure() throws Exception {
        try (MockedStatic<FirebaseFilter> filter = mockStatic(FirebaseFilter.class)) {
            filter.when(() -> FirebaseFilter.extractUserRecordFromRequest(any(), any())).thenReturn(userRecords.get(2));
            Task task = tasks.get(0);

            mvc.perform(delete("/").queryParam("taskId", task.getId())).andExpect(status().isInternalServerError());
            assertEquals(taskRepository.count(), 3);
        }
    }

    private String asJsonString(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    private UserRecord getMockUserRecord(String userUid) {
        UserRecord userRecord = mock(UserRecord.class);
        when(userRecord.getUid()).thenReturn(userUid);
        return userRecord;
    }

    private Project saveProject(String managerId, Set<String> associatesIds) {
        Project project = new Project();
        project.setManagerId(managerId);
        project.setAssociatesIds(associatesIds);
        return projectRepository.save(project);
    }

    private Task buildTask(String title, TaskStatus taskStatus, Project project) {
        Task task = new Task();
        task.setTitle(title);
        task.setTaskStatus(taskStatus);
        task.setProject(project);
        return task;
    }

    private Task saveTask(String title, TaskStatus taskStatus, Project project) {
        Task task = buildTask(title, taskStatus, project);
        return taskRepository.save(task);
    }

    private String getRandomString(Integer count) {
        return RandomStringUtils.randomAlphabetic(count);
    }

}
