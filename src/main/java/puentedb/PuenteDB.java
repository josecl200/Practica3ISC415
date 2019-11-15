package puentedb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PuenteDB {
    private static PuenteDB instance;
    private String URL = "jdbc:h2:tcp://localhost/~/JDBC-T";

    private PuenteDB() {
        registerDriver();
    }

    public static PuenteDB getInstance() {
        if (instance == null) {
            instance = new PuenteDB();
        }
        return instance;
    }

    private void registerDriver() {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException ex) {

        }
    }

    public Connection getConnection() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(URL, "sa", "contracts");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return con;
    }

    public void testConnection() {
        try {
            getConnection().close();
            System.out.println("Conexión realizada con éxito!!");
        } catch (SQLException ex) {

        }
    }
}
