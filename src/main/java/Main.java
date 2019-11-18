import puentedb.Bootstrap;
import puentedb.PuenteDB;
import spark.Spark;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException, NoSuchAlgorithmException {
        Spark.port(8081);
        Spark.staticFileLocation("/public");
        Bootstrap.startDb();
        Bootstrap.createTables();
        PuenteDB.getInstance().testConnection();
        Filter.applyFilters();
        Routes.rutas();
    }
}
