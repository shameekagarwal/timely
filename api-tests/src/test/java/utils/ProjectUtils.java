package utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.NoSuchElementException;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import models.Project;
import models.User;

public class ProjectUtils {

    private static final User ASSOCIATE_ONE = UserBuilder.getAssociateOne();
    private static final User ASSOCIATE_TWO = UserBuilder.getAssociateTwo();

    public static List<Project> getProjects(User user) {
        return RestAssured.given().header("Content-Type", ContentType.JSON).header(UserToken.get(user)).when()
                .get("projects/").then().extract().response().as(new TypeRef<List<Project>>() {
                });
    }

    public static void assertUserHasProject(User user, Project project) {
        List<Project> projects = getProjects(user);
        Project associateProject = projects.stream().filter(v -> v.getId().equals(project.getId())).findFirst().get();
        assertEquals(project, associateProject);
    }

    public static void assertNotUserHasProject(User user, Project project) {
        List<Project> projects = getProjects(user);
        assertThrows(NoSuchElementException.class,
                () -> projects.stream().filter(v -> v.getId().equals(project.getId())).findFirst().get());
    }

    public static void assertProjectPropagation(Project project) {
        List<User> associates = List.of(ASSOCIATE_ONE, ASSOCIATE_TWO);
        associates.forEach(associate -> {
            if (project.getAssociatesIds().contains(associate.getUuid())) {
                assertUserHasProject(associate, project);
            } else {
                assertNotUserHasProject(associate, project);
            }
        });
    }

}
