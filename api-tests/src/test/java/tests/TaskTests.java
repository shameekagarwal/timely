package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import config.Properties;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import models.Error;
import models.Project;
import models.Task;
import models.User;
import utils.ProjectUtils;
import utils.TaskUtils;
import utils.UserBuilder;
import utils.UserToken;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaskTests {

    private static final User ASSOCIATE_ONE = UserBuilder.getAssociateOne();
    private static final User ASSOCIATE_TWO = UserBuilder.getAssociateTwo();
    private static final User MANAGER_ONE = UserBuilder.getManagerOne();

    @BeforeAll
    public void setup() throws Exception {
        RestAssured.baseURI = Properties.getBaseUrl();
    }

    @Test
    public void whenGetTasks_thenOk() {
        TaskUtils.getTasks(ASSOCIATE_ONE).forEach(TaskUtils::assertTaskPropagation);
        TaskUtils.getTasks(ASSOCIATE_TWO).forEach(TaskUtils::assertTaskPropagation);
    }

    @Test
    public void whenCreateTask_thenOk() {
        String title = RandomStringUtils.randomAlphabetic(10);
        Project project = ProjectUtils.getProjects(ASSOCIATE_ONE).get(0);
        String taskStatus = "DONE";
        Task task = new Task();
        task.setProject(project);
        task.setTitle(title);
        task.setTaskStatus(taskStatus);

        Task response = RestAssured.given().header("Content-Type", ContentType.JSON)
                .header(UserToken.get(ASSOCIATE_ONE)).body(task).when().post("tasks/").then().assertThat()
                .statusCode(200).extract().response().as(Task.class);

        response.setId(null);
        task.getProject().setTitle(null);
        assertEquals(task, response);
    }

    @Test
    public void whenManagerCreateTask_thenFail() {
        String title = RandomStringUtils.randomAlphabetic(10);
        Project project = ProjectUtils.getProjects(MANAGER_ONE).get(0);
        String taskStatus = "DONE";
        Task task = new Task();
        task.setProject(project);
        task.setTitle(title);
        task.setTaskStatus(taskStatus);

        Error response = RestAssured.given().header("Content-Type", ContentType.JSON).header(UserToken.get(MANAGER_ONE))
                .body(task).when().post("tasks/").then().assertThat().statusCode(500).extract().response()
                .as(Error.class);

        assertEquals("you don't have sufficient permissions to access this resource", response.getMessage());
    }

    @Test
    public void whenUpdateTask_thenOk() {
        String title = RandomStringUtils.randomAlphabetic(10);
        String taskStatus = "IN_PROGRESS";
        Task task = TaskUtils.getTasks(ASSOCIATE_ONE).get(0);
        task.setTitle(title);
        task.setTaskStatus(taskStatus);

        Task response = RestAssured.given().header("Content-Type", ContentType.JSON)
                .header(UserToken.get(ASSOCIATE_ONE)).body(task).when().put("tasks/").then().assertThat()
                .statusCode(200).extract().response().as(Task.class);

        assertEquals(task, response);
    }

    @Test
    public void whenManagerUpdateTask_thenFail() {
        String title = RandomStringUtils.randomAlphabetic(10);
        String taskStatus = "IN_PROGRESS";
        Task task = TaskUtils.getTasks(MANAGER_ONE).get(0);
        task.setTitle(title);
        task.setTaskStatus(taskStatus);

        Error response = RestAssured.given().header("Content-Type", ContentType.JSON).header(UserToken.get(MANAGER_ONE))
                .body(task).when().post("tasks/").then().assertThat().statusCode(500).extract().response()
                .as(Error.class);

        assertEquals("you don't have sufficient permissions to access this resource", response.getMessage());
    }

    @Test
    public void whenDeleteTask_thenOk() {
        Task task = TaskUtils.getTasks(ASSOCIATE_ONE).get(0);

        String response = RestAssured.given().header("Content-Type", ContentType.JSON)
                .header(UserToken.get(ASSOCIATE_ONE)).queryParam("taskId", task.getId()).body(task).when()
                .delete("tasks/").then().assertThat().statusCode(200).extract().response().asString();

        assertEquals("task with id %s deleted successfully".formatted(task.getId()), response);
    }

    @Test
    public void whenManagerDeleteTask_thenFailure() {
        Task task = TaskUtils.getTasks(MANAGER_ONE).get(0);

        Error response = RestAssured.given().header("Content-Type", ContentType.JSON).header(UserToken.get(MANAGER_ONE))
                .queryParam("taskId", task.getId()).body(task).when().delete("tasks/").then().assertThat()
                .statusCode(500).extract().response().as(Error.class);

        assertEquals("you don't have sufficient permissions to access this resource", response.getMessage());
    }

}
