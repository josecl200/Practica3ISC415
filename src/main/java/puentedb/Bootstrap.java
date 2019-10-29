package puentedb;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

    public static void createTables() throws SQLException {
        System.out.println("Creating tables...");
        String UsuarioDB = "CREATE TABLE IF NOT EXISTS USUARIO" +
                "(" +
                "ID VARCHAR2 PRIMARY KEY NOT NULL," +
                "USERNAME VARCHAR(30)," +
                "NOMBRE VARCHAR(30)," +
                "PASSWORD VARCHAR(30)," +
                "ADMINISTRATOR BOOLEAN," +
                "AUTOR BOOLEAN," +
                "UNIQUE KEY USERNAME_UNIQUE(USERNAME)" +
                ");";
        String EtiquetaDB = "CREATE TABLE IF NOT EXISTS ETIQUETA" +
                "(" +
                "ID VARCHAR2 PRIMARY KEY NOT NULL," +
                "ETIQUETA VARCHAR2 NOT NULL," +
                "UNIQUE KEY TAG_UNIQUE(ETIQUETA)" +
                ");";
        String ArticuloDB = "CREATE TABLE IF NOT EXISTS ARTICULO" +
                "(" +
                "ID VARCHAR2 PRIMARY KEY NOT NULL," +
                "TITULO VARCHAR(30) NOT NULL," +
                "CUERPO VARCHAR2 NOT NULL," +
                "AUTOR OBJECT," +
                "FECHA DATE," +
                "LISTACOMENTARIO ARRAY," +
                "LISTAETIQUETAS ARRAY" +
                ");";
        String ComentarioDB = "CREATE TABLE IF NOT EXISTS COMENTARIO" +
                "(" +
                "ID VARCHAR2 PRIMARY KEY NOT NULL," +
                "COMENTARIO VARCHAR2 NOT NULL," +
                "AUTOR OBJECT," +
                "ARTICULO OBJECT" +
                ");";
        Connection con = DataBaseServices.getInstance().getConnection();
        Statement statement = con.createStatement();
        statement.execute(usersSQL);
        statement.execute(tagsSQL);
        statement.execute(articlesSQL);
        statement.execute(commentsSQL);
        statement.execute(articlesTagsSQL);
        statement.close();

        Usuario.getInstance().createUser(new User("admin", "Admin", "123456", "admin"));

        con.close();
        System.out.println("Tables created!!");
    }
}

