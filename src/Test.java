import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;
import spark.ModelAndView;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Test {

    //MySQL Info for a local vagrant lamp stack
    private static String databaseURL = "jdbc:mysql://127.0.0.1:8889/spark";
    private static String databaseUser = "root";
    private static String databasePassword = "root";


    public static void main(String[] args) throws SQLException {

        JSONTransformer json = new JSONTransformer();
        FreeMarkerTemplateEngine ftl = new FreeMarkerTemplateEngine();

        //SQL Database Stuff
        ConnectionSource connectionSource = getConnectionSource();
        Dao<User, String> userDao = DaoManager.createDao(connectionSource, User.class); //Create Data Access Object
        TableUtils.createTableIfNotExists(connectionSource, User.class); //Create users table iff it doesn't exist

        //Set public folder as HTML file location
        staticFileLocation("/public");

        //Simple GET route
        get("/hello", (req, res) -> "Hello World");

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

        //FreeMarker Template
        get("/templateTest", (request, response) -> {
            Map<String, Object> attributes = new HashMap<String, Object>();
            attributes.put("param1", "HELLO");
            attributes.put("param2", "WORLD");

            return new ModelAndView(attributes, "two_params.ftl");
        }, ftl);

        get("/templateTest/:name/:age", (request, response) -> {
            Map<String, Object> attributes = new HashMap<String, Object>();

            attributes.put("name", request.params(":name"));
            attributes.put("age", request.params(":age"));

            return new ModelAndView(attributes, "NameAndAge.ftl");
        }, ftl);



        //SQL TEST ROUTES

        //This post method works by sending a POST request to a URL like
        //http://localhost:4567/users?username=alex&email=alex@abraham.net
        post("/users", (request, response) -> {
            String username = request.queryParams("username");
            String email = request.queryParams("email");

            //Get password and encrypt
            String password = request.queryParams("pass");
            StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
            password = passwordEncryptor.encryptPassword(password);


            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPassword(password);

            userDao.create(newUser); //Method to add new user to database

            response.status(201);

            return response;
        });

        get("/user/:id/:pass", (request, response) -> {

            User user = userDao.queryForId(request.params(":id"));
            StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
            String password = request.params(":pass");

            if (user != null) {

                if (passwordEncryptor.checkPassword(password, user.getPassword())) {
                    return user;
                } else {
                    response.status(500);
                    return "Wrong Password!";
                }

            } else {
                response.status(404);
                return "User Not Found!";
            }
        }, json);

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
}
