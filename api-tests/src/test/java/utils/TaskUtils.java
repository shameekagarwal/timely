package utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.NoSuchElementException;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import models.Project;
import models.Task;
import models.User;

public class TaskUtils {

    private static final User ASSOCIATE_ONE = UserBuilder.getAssociateOne();
    private static final User ASSOCIATE_TWO = UserBuilder.getAssociateTwo();
    private static final User MANAGER_ONE = UserBuilder.getManagerOne();
    private static final User MANAGER_TWO = UserBuilder.getManagerTwo();

    public static List<Task> getTasks(User user) {
        return RestAssured.given().header("Content-Type", ContentType.JSON).header(UserToken.get(user)).when()
                .get("tasks/").then().extract().response().as(new TypeRef<List<Task>>() {
                });
    }

    public static Project getProjectFromTask(Task task, List<Project> projects) {
        return projects.stream().filter(project -> project.getId().equals(task.getProject().getId())).findFirst().get();
    }

    public static void assertUserHasTask(User user, Task task) {
        List<Task> tasks = getTasks(user);
        Task associateTask = tasks.stream().filter(v -> v.getId().equals(task.getId())).findFirst().get();
        assertEquals(task, associateTask);
    }

    public static void assertNotUserHasTask(User user, Task task) {
        List<Task> tasks = getTasks(user);
        assertThrows(NoSuchElementException.class,
                () -> tasks.stream().filter(v -> v.getId().equals(task.getId())).findFirst().get());
    }

    // 1. derive managers, associates part of task
    // 2. assert changes to a task is reflected for all of them

    public static void assertTaskPropagation(Task task) {
        List<User> managers = List.of(MANAGER_ONE, MANAGER_TWO);
        List<User> associates = List.of(ASSOCIATE_ONE, ASSOCIATE_TWO);

        List<User> taskAssociates = UserBuilder.getAssociatesFromIds(task.getProject().getAssociatesIds());
        User taskManager = UserBuilder.getManagerFromId(task.getProject().getManagerId());

        managers.forEach(manager -> {
            if (taskManager.equals(manager)) {
                assertUserHasTask(manager, task);
            } else {
                assertNotUserHasTask(manager, task);
            }
        });

        associates.forEach(associate -> {
            if (taskAssociates.contains(associate)) {
                assertUserHasTask(associate, task);
            } else {
                assertNotUserHasTask(associate, task);
            }
        });
    }

}
