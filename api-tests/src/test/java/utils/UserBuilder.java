package utils;

import java.util.ArrayList;
import java.util.List;

import models.User;

public class UserBuilder {

    private static final User MANAGER_ONE = new User();
    private static final User MANAGER_TWO = new User();
    private static final User ASSOCIATE_ONE = new User();
    private static final User ASSOCIATE_TWO = new User();

    static {
        MANAGER_ONE.setEmail(System.getenv("MANAGER_ONE_EMAIL"));
        MANAGER_ONE.setPassword(System.getenv("MANAGER_ONE_PASSWORD"));
        MANAGER_ONE.setUuid(System.getenv("MANAGER_ONE_UUID"));

        MANAGER_TWO.setEmail(System.getenv("MANAGER_TWO_EMAIL"));
        MANAGER_TWO.setPassword(System.getenv("MANAGER_TWO_PASSWORD"));
        MANAGER_TWO.setUuid(System.getenv("MANAGER_TWO_UUID"));

        ASSOCIATE_ONE.setEmail(System.getenv("ASSOCIATE_ONE_EMAIL"));
        ASSOCIATE_ONE.setPassword(System.getenv("ASSOCIATE_ONE_PASSWORD"));
        ASSOCIATE_ONE.setUuid(System.getenv("ASSOCIATE_ONE_UUID"));

        ASSOCIATE_TWO.setEmail(System.getenv("ASSOCIATE_TWO_EMAIL"));
        ASSOCIATE_TWO.setPassword(System.getenv("ASSOCIATE_TWO_PASSWORD"));
        ASSOCIATE_TWO.setUuid(System.getenv("ASSOCIATE_TWO_UUID"));
    }

    public static List<User> getAssociatesFromIds(List<String> associateIds) {
        List<User> associates = new ArrayList<>();
        if (associateIds.contains(ASSOCIATE_ONE.getUuid()))
            associates.add(ASSOCIATE_ONE);
        if (associateIds.contains(ASSOCIATE_TWO.getUuid()))
            associates.add(ASSOCIATE_TWO);
        return associates;
    }

    public static User getManagerFromId(String id) {
        if (id.equals(MANAGER_ONE.getUuid()))
            return MANAGER_ONE;
        if (id.equals(MANAGER_TWO.getUuid()))
            return MANAGER_TWO;
        return null;
    }

    public static User getManagerOne() {
        return MANAGER_ONE;
    }

    public static User getManagerTwo() {
        return MANAGER_TWO;
    }

    public static User getAssociateOne() {
        return ASSOCIATE_ONE;
    }

    public static User getAssociateTwo() {
        return ASSOCIATE_TWO;
    }

}