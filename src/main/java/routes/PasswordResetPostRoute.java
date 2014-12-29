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
import java.util.Map;

import static spark.Spark.modelAndView;

public class PasswordResetPostRoute implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request request, Response response) {
        User user = request.session(true).attribute("user");

        String oldPassword = request.queryParams("oldPassword");
        String newPassword = request.queryParams("newPassword");
        String newPassword2 = request.queryParams("newPassword2");

        if (oldPassword.equals("")) {
            response.status(400);
            Map<String, Object> attributes = new HashMap<String, Object>();
            attributes.put("badMessage", "You forgot the old password!");
            attributes.put("user", user);
            attributes.put("oldPassword", oldPassword);
            attributes.put("newPassword", newPassword);
            attributes.put("newPassword2", newPassword2);
            return modelAndView(attributes, "me.ftl");
        }

        if (newPassword.equals("") || newPassword2.equals("")) {
            response.status(400);
            Map<String, Object> attributes = new HashMap<String, Object>();
            attributes.put("badMessage", "You forgot the new passwords!");
            attributes.put("user", user);
            attributes.put("oldPassword", oldPassword);
            attributes.put("newPassword", newPassword);
            attributes.put("newPassword2", newPassword2);
            return modelAndView(attributes, "me.ftl");
        }

        if (!validatePassword(oldPassword, user.getPassword())) {
            response.status(400);
            Map<String, Object> attributes = new HashMap<String, Object>();
            attributes.put("badMessage", "Old password didn't match!");
            attributes.put("user", user);
            attributes.put("oldPassword", oldPassword);
            attributes.put("newPassword", newPassword);
            attributes.put("newPassword2", newPassword2);
            return modelAndView(attributes, "me.ftl");
        }

        if (!newPassword.equals(newPassword2)) {
            response.status(400);
            Map<String, Object> attributes = new HashMap<String, Object>();
            attributes.put("badMessage", "New passwords don't match!");
            attributes.put("user", user);
            attributes.put("oldPassword", oldPassword);
            attributes.put("newPassword", newPassword);
            attributes.put("newPassword2", newPassword2);
            return modelAndView(attributes, "me.ftl");
        }

        //Everything is good! Let's change the account password
        newPassword = hashPassword(newPassword);
        user.setPassword(newPassword);
        DBC.updateUser(user);

        response.status(200);
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("goodMessage", "Successfully changed password!");
        attributes.put("user", user);
        return modelAndView(attributes, "me.ftl");
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

    private String hashPassword(String password)
    {
        try {
            return PasswordHash.createHash(password);
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
