package main.java.routes;

import main.java.models.DBC;
import main.java.models.PasswordHash;
import main.java.models.User;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.modelAndView;

public class LoginPostRoute implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request request, Response response) {

        String email = request.queryParams("email");
        String password = request.queryParams("password");
        User user = getUser(email, password);

        if (user != null) {
            request.session(true).attribute("user", user);
            response.redirect("/me");
            return new ModelAndView(null, "redirect.ftl"); //Blank ModelAndView since we will use the ModelAndView
            //from /me
        } else {
            response.status(401);

            Map<String, Object> attributes = new HashMap<String, Object>();
            attributes.put("message", "Invalid username or password");
            attributes.put("email", email);
            return modelAndView(attributes, "login.ftl");
        }

    }

    private User getUser(String email, String password)
    {

        List<User> userList = DBC.queryUser("email", email);

        if (userList.size() > 0) {
            User user = userList.get(0);

            if (validatePassword(password, user.getPassword())) {
                return user;
            } else {
                return null;
            }
        } else {
            return null;
        }

    }

    private boolean validatePassword(String password, String hash)
    {
        try {
            return PasswordHash.validatePassword(password, hash);
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }

    }
}