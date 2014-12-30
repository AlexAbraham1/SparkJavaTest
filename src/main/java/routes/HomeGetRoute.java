package main.java.routes;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

public class HomeGetRoute implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request request, Response response) {

        if (request.session(true).attribute("user") != null) { //User is logged in. Redirect to /me
            response.status(201);
            response.redirect("/me");
            return new ModelAndView(null, "redirect.ftl");
        }

        //Otherwise, return the home page
        return new ModelAndView(null, "home.ftl");
    }
}