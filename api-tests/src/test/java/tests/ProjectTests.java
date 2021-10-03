package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import config.Properties;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import models.Error;
import models.Project;
import models.User;
import utils.ProjectUtils;
import utils.UserBuilder;
import utils.UserToken;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProjectTests {

    private static final User ASSOCIATE_ONE = UserBuilder.getAssociateOne();
    private static final User ASSOCIATE_TWO = UserBuilder.getAssociateTwo();
    private static final User MANAGER_ONE = UserBuilder.getManagerOne();
    private static final User MANAGER_TWO = UserBuilder.getManagerTwo();

    @BeforeAll
    public void setup() throws Exception {
        RestAssured.baseURI = Properties.getBaseUrl();
    }

    @Test
    public void whenGetProjects_thenOk() {
        ProjectUtils.getProjects(MANAGER_ONE).forEach(ProjectUtils::assertProjectPropagation);
        ProjectUtils.getProjects(MANAGER_TWO).forEach(ProjectUtils::assertProjectPropagation);
    }

    @Test
    public void whenCreateProject_thenOk() {
        List<String> associatesIds = List.of(ASSOCIATE_ONE.getUuid());
        String title = RandomStringUtils.randomAlphabetic(10);
        Project project = new Project();
        project.setTitle(title);
        project.setAssociatesIds(associatesIds);
        project.setManagerId(MANAGER_ONE.getUuid());

        Project response = RestAssured.given().header("Content-Type", ContentType.JSON)
                .header(UserToken.get(MANAGER_ONE)).body(project).when().post("projects/").then().assertThat()
                .statusCode(200).extract().response().as(Project.class);

        response.setId(null);
        assertEquals(project, response);
    }

    @Test
    public void whenAssociateCreateProject_thenFail() {
        List<String> associatesIds = List.of(ASSOCIATE_ONE.getUuid());
        String title = RandomStringUtils.randomAlphabetic(10);
        Project project = new Project();
        project.setTitle(title);
        project.setAssociatesIds(associatesIds);
        project.setManagerId(ASSOCIATE_ONE.getUuid());

        Error response = RestAssured.given().header("Content-Type", ContentType.JSON)
                .header(UserToken.get(ASSOCIATE_ONE)).body(project).when().post("projects/").then().assertThat()
                .statusCode(500).extract().response().as(Error.class);

        assertEquals("you don't have sufficient permissions to access this resource", response.getMessage());
    }

    @Test
    public void whenUpdateProject_thenOk() {
        String title = RandomStringUtils.randomAlphabetic(10);
        Project project = ProjectUtils.getProjects(MANAGER_ONE).get(0);
        project.setTitle(title);
        project.setAssociatesIds(List.of(ASSOCIATE_TWO.getUuid()));

        Project response = RestAssured.given().header("Content-Type", ContentType.JSON)
                .header(UserToken.get(MANAGER_ONE)).body(project).when().put("projects/").then().assertThat()
                .statusCode(200).extract().response().as(Project.class);

        assertEquals(response, project);
    }

    @Test
    public void whenAssociateUpdateProject_thenFail() {
        String title = RandomStringUtils.randomAlphabetic(10);
        Project project = ProjectUtils.getProjects(ASSOCIATE_ONE).get(0);
        project.setTitle(title);
        project.setAssociatesIds(List.of(ASSOCIATE_TWO.getUuid()));

        Error response = RestAssured.given().header("Content-Type", ContentType.JSON)
                .header(UserToken.get(ASSOCIATE_ONE)).body(project).when().put("projects/").then().assertThat()
                .statusCode(500).extract().response().as(Error.class);

        assertEquals("you don't have sufficient permissions to access this resource", response.getMessage());
    }

    @Test
    public void whenDeleteProject_thenOk() {
        Project project = ProjectUtils.getProjects(MANAGER_ONE).get(0);

        String response = RestAssured.given().header("Content-Type", ContentType.JSON)
                .queryParam("projectId", project.getId()).header(UserToken.get(MANAGER_ONE)).body(project).when()
                .delete("projects/").then().assertThat().statusCode(200).extract().response().asString();

        assertEquals("project with id %s deleted successfully".formatted(project.getId()), response);
        ProjectUtils.assertNotUserHasProject(MANAGER_ONE, project);
    }

    @Test
    public void whenAssociateDeleteProject_thenFail() {
        Project project = ProjectUtils.getProjects(ASSOCIATE_ONE).get(0);

        Error response = RestAssured.given().header("Content-Type", ContentType.JSON)
                .queryParam("projectId", project.getId()).header(UserToken.get(ASSOCIATE_ONE)).body(project).when()
                .delete("projects/").then().assertThat().statusCode(500).extract().response().as(Error.class);

        assertEquals("you don't have sufficient permissions to access this resource", response.getMessage());
    }

}
