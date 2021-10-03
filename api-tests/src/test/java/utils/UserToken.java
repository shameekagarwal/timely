package utils;

import config.Properties;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import models.User;

public class UserToken {

    public static Header get(User user) {
        String response = RestAssured.given().urlEncodingEnabled(false).header("Content-Type", ContentType.JSON)
                .body(user).when().post(Properties.FIREBASE_AUTH_SIGNIN_URL).then().contentType(ContentType.JSON)
                .assertThat().statusCode(200).extract().response().asString();
        JsonPath json = new JsonPath(response);
        return new Header("Authorization", json.getString("idToken"));
    }

}
