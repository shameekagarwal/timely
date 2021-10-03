package utils;

import models.User;

public class UserBuilder {

    public static User getManagerOne() {
        User user = new User();
        user.setEmail(System.getenv("MANAGER_ONE_EMAIL"));
        user.setPassword(System.getenv("MANAGER_ONE_PASSWORD"));
        return user;
    }

    public static User getAssociateOne() {
        User user = new User();
        user.setEmail(System.getenv("ASSOCIATE_ONE_EMAIL"));
        user.setPassword(System.getenv("ASSOCIATE_ONE_PASSWORD"));
        return user;
    }

}
