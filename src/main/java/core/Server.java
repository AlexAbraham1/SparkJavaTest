package main.java.core;

import main.java.models.*;
import main.java.routes.*;

import spark.Filter;
import static spark.Spark.*;

public class Server {

    private static final String[] protectedRoutes = {"/me", "/passwordReset"};

    public static void main(String[] args) {

        FreeMarkerTemplateEngine ftl = new FreeMarkerTemplateEngine();

        DBC.initialize(); //Initialize Database Connection

        //Set public folder as HTML file location
        staticFileLocation("/main/resources/public");

        //Filter for authentication
        setupProtectedFilters();

        get("/", new HomeGetRoute(), ftl);

        get("/signup", new SignupGetRoute(), ftl);

        post("/signup", new SignupPostRoute(), ftl);

        get("/login", new LoginGetRoute(), ftl);

        post("/login", new LoginPostRoute(), ftl);

        get("/me", new ProfileGetRoute(), ftl);

        post("/passwordReset", new PasswordResetPostRoute(), ftl);

        get("/logout", new LogoutGetRoute(), ftl);
    }

    private static void setupProtectedFilters()
    {
        Filter f = (request, response) -> {

            if (request.session(true).attribute("user") == null) { //No user in session
                halt(401, "Not Logged In!");
            }

        };

        for (String route : protectedRoutes) {
            before(route, f);
        }
    }
}
