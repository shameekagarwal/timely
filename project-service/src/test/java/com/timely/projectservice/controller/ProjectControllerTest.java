package com.timely.projectservice.controller;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.timely.projectservice.filter.FirebaseFilter;
import com.timely.projectservice.model.Project;
import com.timely.projectservice.model.User;
import com.timely.projectservice.repository.ProjectRepository;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerTest {

    @MockBean
    private FirebaseAuth firebaseAuth;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectRepository projectRepository;

    // project 1, project 2
    List<Project> projects;

    // manager 1, associcate 1, associcate 2
    List<UserRecord> userRecords;

    // project 1 - manager 1, associcate 1
    // project 2 - manager 1, associcate 2

    @BeforeEach
    public void setup() throws Exception {
        List<String> managerIds = List.of(getRandomString(10));
        List<String> associatesIds = List.of(getRandomString(10), getRandomString(10));

        projects = List.of(saveProject(managerIds.get(0), Set.of(associatesIds.get(0))),
                saveProject(managerIds.get(0), Set.of(associatesIds.get(1))));
        userRecords = List.of(getMockUserRecord(managerIds.get(0), User.MANAGER_ROLE),
                getMockUserRecord(associatesIds.get(0), User.ASSOCIATE_ROLE),
                getMockUserRecord(associatesIds.get(1), User.ASSOCIATE_ROLE));

        projects.forEach(projectRepository::save);
    }

    @AfterEach
    public void cleanup() {
        projectRepository.deleteAll();
    }

    @Test
    public void whenPing_thenSuccess() throws Exception {
        mvc.perform(get("/ping")).andExpect(status().isOk()).andExpect(content().string("pong from project-service"));
    }

    @Test
    public void whenGetProjects_thenSuccess() throws Exception {

        try (MockedStatic<FirebaseFilter> filter = mockStatic(FirebaseFilter.class)) {
            filter.when(() -> FirebaseFilter.extractUserRecordFromRequest(any(), any())).thenReturn(userRecords.get(0));
            mvc.perform(get("/").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].title").value(projects.get(0).getTitle()))
                    .andExpect(jsonPath("$[1].title").value(projects.get(1).getTitle()));
        }

        try (MockedStatic<FirebaseFilter> filter = mockStatic(FirebaseFilter.class)) {
            filter.when(() -> FirebaseFilter.extractUserRecordFromRequest(any(), any())).thenReturn(userRecords.get(1));
            mvc.perform(get("/").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].title").value(projects.get(0).getTitle()));
        }

        try (MockedStatic<FirebaseFilter> filter = mockStatic(FirebaseFilter.class)) {
            filter.when(() -> FirebaseFilter.extractUserRecordFromRequest(any(), any())).thenReturn(userRecords.get(2));
            mvc.perform(get("/").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].title").value(projects.get(1).getTitle()));
        }

    }

    @Test
    public void whenCreateProject_thenSuccess() throws Exception {
        try (MockedStatic<FirebaseFilter> filter = mockStatic(FirebaseFilter.class)) {
            filter.when(() -> FirebaseFilter.extractUserRecordFromRequest(any(), any())).thenReturn(userRecords.get(0));
            Project project = buildProject(userRecords.get(0).getUid(), Set.of());

            mvc.perform(post("/").contentType(MediaType.APPLICATION_JSON).content(asJsonString(project)))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.title").value(project.getTitle()))
                    .andExpect(jsonPath("$.managerId").value(project.getManagerId()));
            assertEquals(3, projectRepository.count());
        }
    }

    @Test
    public void whenCreateProject_thenFailure() throws Exception {
        try (MockedStatic<FirebaseFilter> filter = mockStatic(FirebaseFilter.class)) {
            filter.when(() -> FirebaseFilter.extractUserRecordFromRequest(any(), any())).thenReturn(userRecords.get(0));
            Project project = buildProject(userRecords.get(0).getUid(), Set.of(userRecords.get(0).getUid()));

            mvc.perform(post("/").contentType(MediaType.APPLICATION_JSON).content(asJsonString(project)))
                    .andExpect(status().isBadRequest());
            assertEquals(2, projectRepository.count());
        }
    }

    @Test
    public void whenUpdateProject_thenSuccess() throws Exception {
        try (MockedStatic<FirebaseFilter> filter = mockStatic(FirebaseFilter.class)) {
            filter.when(() -> FirebaseFilter.extractUserRecordFromRequest(any(), any())).thenReturn(userRecords.get(0));
            Project project = projects.get(0);
            project.setTitle(getRandomString(10));

            mvc.perform(put("/").contentType(MediaType.APPLICATION_JSON).content(asJsonString(project)))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.title").value(project.getTitle()));
            assertEquals(2, projectRepository.count());
            Project savedProject = projectRepository.findById(project.getId()).get();
            assertEquals(project.getTitle(), savedProject.getTitle());
        }
    }

    @Test
    public void whenUpdateProject_thenFailure() throws Exception {
        try (MockedStatic<FirebaseFilter> filter = mockStatic(FirebaseFilter.class)) {
            filter.when(() -> FirebaseFilter.extractUserRecordFromRequest(any(), any())).thenReturn(userRecords.get(1));
            Project project = projects.get(0);
            project.setTitle(getRandomString(10));

            mvc.perform(put("/").contentType(MediaType.APPLICATION_JSON).content(asJsonString(project)))
                    .andExpect(status().isInternalServerError());
            assertEquals(2, projectRepository.count());
            Project savedProject = projectRepository.findById(project.getId()).get();
            assertNotEquals(project.getTitle(), savedProject.getTitle());
        }
    }

    @Test
    public void whenDeleteProject_thenSuccess() throws Exception {
        try (MockedStatic<FirebaseFilter> filter = mockStatic(FirebaseFilter.class)) {
            filter.when(() -> FirebaseFilter.extractUserRecordFromRequest(any(), any())).thenReturn(userRecords.get(0));
            Project project = projects.get(0);

            mvc.perform(delete("/").queryParam("projectId", project.getId())).andExpect(status().isOk())
                    .andExpect(content().string("project with id %s deleted successfully".formatted(project.getId())));
            assertEquals(1, projectRepository.count());
        }
    }

    @Test
    public void whenDeleteProject_thenFailure() throws Exception {
        try (MockedStatic<FirebaseFilter> filter = mockStatic(FirebaseFilter.class)) {
            filter.when(() -> FirebaseFilter.extractUserRecordFromRequest(any(), any())).thenReturn(userRecords.get(1));
            Project project = projects.get(0);

            mvc.perform(delete("/").queryParam("projectId", project.getId()))
                    .andExpect(status().isInternalServerError());
            assertEquals(2, projectRepository.count());
        }
    }

    private String asJsonString(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    private UserRecord getMockUserRecord(String userUid, String role) throws Exception {
        UserRecord userRecord = mock(UserRecord.class);
        when(userRecord.getUid()).thenReturn(userUid);
        Map<String, Object> claims = new HashMap<>();
        claims.put(role, true);
        when(firebaseAuth.getUser(userUid)).thenReturn(userRecord);
        when(userRecord.getCustomClaims()).thenReturn(claims);
        return userRecord;
    }

    private Project buildProject(String managerId, Set<String> associatesIds) {
        Project project = new Project();
        project.setTitle(getRandomString(10));
        project.setManagerId(managerId);
        project.setAssociatesIds(associatesIds);
        return project;
    }

    private Project saveProject(String managerId, Set<String> associatesIds) {
        Project project = buildProject(managerId, associatesIds);
        return projectRepository.save(project);
    }

    private String getRandomString(Integer count) {
        return RandomStringUtils.randomAlphabetic(count);
    }

}
