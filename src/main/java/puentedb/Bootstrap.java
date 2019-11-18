package puentedb;

import clases.Usuario;
import org.h2.tools.Server;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Bootstrap{

    public static void startDb() throws SQLException {
        Server.createTcpServer("-tcpPort", "9094", "-tcpAllowOthers").start();
    }

    public static void stopDb() throws SQLException {
        Server.shutdownTcpServer("tcp://localhost:9094", "contracts", true, true);
    }

    public static void createTables() throws SQLException, NoSuchAlgorithmException {
        System.out.println("Creating tables...");
        String UsuarioDB = "CREATE TABLE IF NOT EXISTS USUARIO" +
                "(" +
                "ID BIGINT PRIMARY KEY NOT NULL," +
                "USERNAME VARCHAR(30) UNIQUE," +
                "NOMBRE VARCHAR(30)," +
                "PASSWORD TINYBLOB," +
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
                "TITULO VARCHAR(100) NOT NULL," +
                "CUERPO VARCHAR2 NOT NULL," +
                "AUTOR BIGINT NOT NULL REFERENCES USUARIO(ID)," +
                "FECHA TIMESTAMP NOT NULL" +
                ");";
        String ComentarioDB = "CREATE TABLE IF NOT EXISTS COMENTARIO" +
                "(" +
                "ID BIGINT PRIMARY KEY NOT NULL," +
                "COMENTARIO VARCHAR2 NOT NULL," +
                "AUTOR BIGINT NOT NULL REFERENCES USUARIO(ID)," +
                "ARTICULO BIGINT NOT NULL REFERENCES ARTICULO(ID)," +
                "FECHA TIMESTAMP NOT NULL"+
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
        MessageDigest md   = MessageDigest.getInstance("SHA-224");
        byte[] hashPassEnt = md.digest("toor".getBytes(StandardCharsets.UTF_8));
        if(PuenteUser.getInstance().crearUsuario(new Usuario(0,"root","admin",hashPassEnt,true,true))){
            System.out.println("Tables created!!");
        }else{
            System.out.println("DB ERROR");
        }


        con.close();
    }
}

