import spark.Spark;
import spark.Spark.*;

import java.sql.SQLException;

import static spark.Spark.get;
import static spark.Spark.staticFileLocation;

public class Main {
    public static void main(String[] args){
        Spark.port(8080);
        staticFileLocation("/public");


    }
}
