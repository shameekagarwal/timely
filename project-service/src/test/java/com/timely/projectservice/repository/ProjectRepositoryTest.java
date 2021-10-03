package com.timely.projectservice.repository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import com.timely.projectservice.model.Project;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

@DataMongoTest
public class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    List<String> managerIds;
    List<String> associatesIds;
    List<Project> projects;

    // project 1 - manager 1, associcate 1, associcate 2, associcate 3
    // project 2 - manager 1, associcate 1, associcate 2
    // project 3 - manager 1, associcate 1
    // project 4 - manager 2, associcate 1, asscoiate 2
    // project 5 - manager 2, associcate 3

    @BeforeEach
    public void setup() {
        String managerIdOne = RandomStringUtils.randomAlphabetic(10);
        String managerIdTwo = RandomStringUtils.randomAlphabetic(10);
        String associateIdOne = RandomStringUtils.randomAlphabetic(10);
        String associateIdTwo = RandomStringUtils.randomAlphabetic(10);
        String associateIdThree = RandomStringUtils.randomAlphabetic(10);

        Project projectOne = saveProject(managerIdOne, Set.of(associateIdOne, associateIdTwo, associateIdThree));
        Project projectTwo = saveProject(managerIdOne, Set.of(associateIdOne, associateIdTwo));
        Project projectThree = saveProject(managerIdOne, Set.of(associateIdOne));
        Project projectFour = saveProject(managerIdTwo, Set.of(associateIdOne, associateIdTwo));
        Project projectFive = saveProject(managerIdTwo, Set.of(associateIdThree));

        managerIds = List.of(managerIdOne, managerIdTwo);
        associatesIds = List.of(associateIdOne, associateIdTwo, associateIdThree);
        projects = List.of(projectOne, projectTwo, projectThree, projectFour, projectFive);
    }

    private Project saveProject(String managerId, Set<String> associatesIds) {
        Project project = new Project();
        project.setManagerId(managerId);
        project.setAssociatesIds(associatesIds);
        return mongoTemplate.save(project);
    }

    private Integer getProjectsSize(String userId) {
        return projectRepository.findByAssociatesIdsContainingOrManagerId(userId, userId).size();
    }

    private Executable getProjectExecutable(String projectId, String userId) {
        return () -> projectRepository.findByIdAndManagerId(projectId, userId).get();
    }

    @Test
    public void whenFindByAssociatesIdsContainingOrManagerId_thenSuccess() {
        assertEquals(3, getProjectsSize(managerIds.get(0)));
        assertEquals(2, getProjectsSize(managerIds.get(1)));
        assertEquals(4, getProjectsSize(associatesIds.get(0)));
        assertEquals(3, getProjectsSize(associatesIds.get(1)));
        assertEquals(2, getProjectsSize(associatesIds.get(2)));
    }

    @Test
    public void whenFindByIdAndManagerId_thenSuccess() {
        assertDoesNotThrow(getProjectExecutable(projects.get(4).getId(), managerIds.get(1)));

        assertThrows(NoSuchElementException.class, getProjectExecutable(projects.get(4).getId(), associatesIds.get(2)));
        assertThrows(NoSuchElementException.class, getProjectExecutable(projects.get(4).getId(), managerIds.get(0)));
    }

}
