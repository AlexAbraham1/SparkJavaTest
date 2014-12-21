import spark.ModelAndView;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static spark.Spark.*;

public class Test {
    public static void main(String[] args) {

        ObjToJSON o2j = new ObjToJSON();
        FreeMarkerTemplateEngine ftl = new FreeMarkerTemplateEngine();

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
            attributes.put("title", "Template Test");
            attributes.put("param1", "HELLO");
            attributes.put("param2", "WORLD");

            return new ModelAndView(attributes, "two_params.ftl");
        }, ftl);

        get("/templateTest/:name/:age", (request, response) -> {
            Map<String, Object> attributes = new HashMap<String, Object>();

            attributes.put("title", "Name Age Test");
            attributes.put("name", request.params(":name"));
            attributes.put("age", request.params(":age"));

            return new ModelAndView(attributes, "NameAndAge.ftl");
        }, ftl);

        //test ObjToJSON
        class Person
        {
            private Map<String, Object> map = new TreeMap<String, Object>();


            public Person(String name)
            {
                map.put("Name", name);
            }

            public Person(String name, int age)
            {
                map.put("Name", name);
                map.put("Age", age);
            }
            public Person(String name, int age, String color)
            {
                map.put("Name", name);
                map.put("Age", age);
                map.put("Favorite Color", color);
            }

            public Map<String, Object> getData()
            {
                return map;
            }
        }

        get("/jsonTest", (request, response) -> {
           return new Person("Alex Abraham", 20, "Blue").getData();
        }, o2j);
    }
}
