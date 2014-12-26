import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.sun.org.apache.xpath.internal.operations.Mod;
import spark.Filter;
import spark.ModelAndView;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class Test {

    //MySQL Info for a local vagrant lamp stack
    private static String databaseURL = "jdbc:mysql://127.0.0.1:8889/spark";
    private static String databaseUser = "root";
    private static String databasePassword = "root";
    private static Dao<User, String> userDao;


    public static void main(String[] args) throws SQLException {

        JSONTransformer json = new JSONTransformer();
        FreeMarkerTemplateEngine ftl = new FreeMarkerTemplateEngine();

        //SQL Database Stuff
        ConnectionSource connectionSource = getConnectionSource();
        userDao = DaoManager.createDao(connectionSource, User.class); //Create Data Access Object
        TableUtils.createTableIfNotExists(connectionSource, User.class); //Create users table iff it doesn't exist

        //Set public folder as HTML file location
        staticFileLocation("/public");

        //Filter for authentication
        setupProtectedFilters();

        //Simple GET route
        get("/", (request, response) -> new ModelAndView(null, "home.ftl"), ftl);

        //route with parameter
        get("/hello/:name", (request, response) -> {
            return "Hello: " + request.params(":name");
        });

        //route with splat (wildcard) parameters
        get("/me/*/test/*", (request, response) -> {
            String result = "<html><head><title>TEST</title></head><body>" +
                            "<h1>PARAMETER 1</h1>" +
                            "<h2>" + request.splat()[0] + "</h2>" +
                            "<hr /><h1>PARAMETER 2</h1>" +
                            "<h2>" + request.splat()[1] + "</h2>" +
                            "</body></html>";
            response.type("text/html");
            return result;
        });

        get("/signup", (request, response) -> {
            return new ModelAndView(null, "signup.ftl");
        }, ftl);

        post("/signup", (request, response) -> {
            String name = request.queryParams("fullname").trim();
            String email = request.queryParams("email").trim();
            String password = request.queryParams("password");
            String password2 = request.queryParams("password2");

            if (name.equals("")) {
                response.status(400);
                Map<String, Object> attributes = new HashMap<String, Object>();
                attributes.put("message", "You forgot your name!");
                attributes.put("fullname", name);
                attributes.put("email", email);
                return modelAndView(attributes, "signup.ftl");
            }

            if (email.equals("")) {
                response.status(400);
                Map<String, Object> attributes = new HashMap<String, Object>();
                attributes.put("message", "You forgot your email!");
                attributes.put("fullname", name);
                attributes.put("email", email);
                return modelAndView(attributes, "signup.ftl");
            }

            if (userExists(email)) {
                response.status(400);
                Map<String, Object> attributes = new HashMap<String, Object>();
                attributes.put("message", "That email is already in out database!");
                attributes.put("fullname", name);
                attributes.put("email", email);
                return modelAndView(attributes, "signup.ftl");
            }

            if (password.equals("") || password2.equals("")) {
                response.status(400);
                Map<String, Object> attributes = new HashMap<String, Object>();
                attributes.put("message", "You forgot passwords!");
                attributes.put("fullname", name);
                attributes.put("email", email);
                return modelAndView(attributes, "signup.ftl");
            }

            //Passwords don't match
            if (!password.equals(password2)) {
                response.status(422);
                Map<String, Object> attributes = new HashMap<String, Object>();
                attributes.put("message", "Passwords don't match!");
                attributes.put("fullname", name);
                attributes.put("email", email);
                return modelAndView(attributes, "signup.ftl");
            }

            //Everything is good! Let's create the account

            password = hashPassword(password);

            User newUser = new User();
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setPassword(password);

            addUser(newUser);

            //Add user to session and go to /me
            request.session(true).attribute("user", newUser);
            response.status(201);
            response.redirect("/me");
            return new ModelAndView(null, "redirect.ftl"); //Blank ModelAndView since we will use the ModelAndView
                                                           //from /me

        }, ftl);

        get("/login", (request, response) -> {
            return new ModelAndView(null, "login.ftl");
        }, ftl);


        post("/login", (request, response) -> {
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
        }, ftl);

        get("/me", (request, response) -> {
            Map<String, Object> attributes = new HashMap<String, Object>();

            User user = request.session(true).attribute("user");
            attributes.put("user", user);
            return new ModelAndView(attributes, "me.ftl");
        }, ftl);

        post("/passwordReset", (request, response) -> {

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
            updateUser(user);

            response.status(200);
            Map<String, Object> attributes = new HashMap<String, Object>();
            attributes.put("goodMessage", "Successfully changed password!");
            attributes.put("user", user);
            return modelAndView(attributes, "me.ftl");
        }, ftl);

        get("/logout", (request, response) -> {
            request.session(true).attribute("user", null);
            response.status(200);
            response.redirect("/");
            return new ModelAndView(null, "redirect.ftl");
        }, ftl);

    }

    private static ConnectionSource getConnectionSource() {

        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(databaseURL);
            ((JdbcConnectionSource)connectionSource).setUsername(databaseUser);
            ((JdbcConnectionSource)connectionSource).setPassword(databasePassword);
            return connectionSource;
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }

    }

    private static User getUser(String email, String password)
    {
        try {
            List<User> userList = userDao.queryForEq("email", email);

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

        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static void addUser(User user)
    {
        try {
            userDao.create(user);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static void updateUser(User user)
    {
        try {
            userDao.update(user);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static String hashPassword(String password)
    {
        try {
            return PasswordHash.createHash(password);
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static boolean validatePassword(String password, String hash)
    {
        try {
            return PasswordHash.validatePassword(password, hash);
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }

    }

    private static boolean userExists(String email)
    {
        try {
            List<User> userList = userDao.queryForEq("email", email);

            return (userList.size() > 0);

        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static void setupProtectedFilters()
    {
        String[] routes = {"/me", "/passwordReset"};

        Filter f = (request, response) -> {

            if (request.session(true).attribute("user") == null) { //No user in session
                halt(401, "Not Logged In!");
            }
        };

        for (String route : routes) {
            before(route, f);
        }
    }
}
