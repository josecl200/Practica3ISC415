package puentedb;

import clases.Usuario;
import org.h2.tools.Server;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Bootstrap{

    public static void startDb() throws SQLException {
        Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers").start();
    }

    public static void stopDb() throws SQLException {
        Server.shutdownTcpServer("tcp://localhost:9092", "contracts", true, true);
    }

    public static void createTables() throws SQLException {
        System.out.println("Creating tables...");
        String UsuarioDB = "CREATE TABLE IF NOT EXISTS USUARIO" +
                "(" +
                "ID BIGINT PRIMARY KEY NOT NULL," +
                "USERNAME VARCHAR(30) UNIQUE," +
                "NOMBRE VARCHAR(30)," +
                "PASSWORD VARCHAR(30)," +
                "ADMINISTRATOR BOOLEAN," +
                "AUTOR BOOLEAN," +
                "UNIQUE KEY USERNAME_UNIQUE(USERNAME)" +
                ");";
        String EtiquetaDB = "CREATE TABLE IF NOT EXISTS ETIQUETA" +
                "(" +
                "ID BIGINT PRIMARY KEY NOT NULL," +
                "ETIQUETA VARCHAR2 NOT NULL," +
                "UNIQUE KEY TAG_UNIQUE(ETIQUETA)" +
                ");";
        String ArticuloDB = "CREATE TABLE IF NOT EXISTS ARTICULO" +
                "(" +
                "ID BIGINT PRIMARY KEY NOT NULL," +
                "TITULO VARCHAR(30) NOT NULL," +
                "CUERPO VARCHAR2 NOT NULL," +
                "AUTOR OBJECT," +
                "FECHA DATE," +
                ");";
        String ComentarioDB = "CREATE TABLE IF NOT EXISTS COMENTARIO" +
                "(" +
                "ID BIGINT PRIMARY KEY NOT NULL," +
                "COMENTARIO VARCHAR2 NOT NULL," +
                "AUTOR OBJECT," +
                "ARTICULO OBJECT" +
                ");";

        String EtiqArtDB = "CREATE TABLE IF NOT EXISTS ARTETIQUETA" +
                "(" +
                "ID_ARTICULO BIGINT," +
                "ID_ETIQUETA BIGINT," +
                "FOREIGN KEY(ID_ARTICULO) REFERENCES ARTICULO(ID)," +
                "FOREIGN KEY(ID_ETIQUETA) REFERENCES ETIQUETA(ID)," +
                "PRIMARY KEY(ID_ARTICULO, ID_ETIQUETA)" +
                ");";
        Connection con = PuenteDB.getInstance().getConnection();
        Statement statement = con.createStatement();
        statement.execute(UsuarioDB);
        statement.execute(EtiquetaDB);
        statement.execute(ArticuloDB);
        statement.execute(ComentarioDB);
        statement.execute(EtiqArtDB);
        statement.close();

        PuenteUser.getInstance().crearUsuario(new Usuario(0,"root","admin","toor",true,true));

        con.close();
        System.out.println("Tables created!!");
    }
}

