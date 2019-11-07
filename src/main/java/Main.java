import spark.Spark;

import static spark.Spark.staticFileLocation;

public class Main {
    public static void main(String[] args){
        Spark.port(8080);
        staticFileLocation("/public");
        Routes.rutas();
    }
}
