package main.java.routes;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

/**
 * Created by alexabraham on 12/28/14.
 */
public class LogoutGetRoute implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response response) {
        request.session(true).attribute("user", null);
        response.status(200);
        response.redirect("/");
        return new ModelAndView(null, "redirect.ftl");
    }
}
