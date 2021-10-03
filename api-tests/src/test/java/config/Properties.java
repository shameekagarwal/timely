package config;

public class Properties {

    public static final String FIREBASE_AUTH_SIGNIN_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=AIzaSyAdjOrPny7f1qkGlcFRlsCuRdUz8R1iq7Q";

    public static String getBaseUrl() throws Exception {
        return System.getenv("API_BASE_URL");
    }

}