import puentedb.Bootstrap;
import puentedb.PuenteDB;
import spark.Spark;

import java.sql.SQLException;

import static spark.Spark.staticFileLocation;

public class Main {
    public static void main(String[] args) throws SQLException {
        Spark.port(8080);
        Spark.staticFileLocation("/public");
        Bootstrap.startDb();
        Bootstrap.createTables();
        PuenteDB.getInstance().testConnection();
        Routes.rutas();
    }
}
