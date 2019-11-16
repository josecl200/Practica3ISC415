package puentedb;

import clases.Articulo;
import clases.Comentario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PuenteComentario {
    private static PuenteComentario instance;

    public static PuenteComentario getInstance(){
        if(instance==null){
            instance=new PuenteComentario();
        }
        return instance;
    }

    public ArrayList<Comentario> cargarComentarios(long id_articulo){
        ArrayList<Comentario> comments = new ArrayList<>();
        Connection con=null;
        try{
            String query = "SELECT * FROM COMENTARIO WHERE ARTICULO = ? ORDER BY fecha"; //TODO: revisar con modelo
            con = PuenteDB.getInstance().getConnection();
            PreparedStatement execQuery = con.prepareCall(query);
            execQuery.setLong(1,id_articulo);
            ResultSet comentariosRes = execQuery.executeQuery();
            while (comentariosRes.next()){
                Comentario com = getComentario(comentariosRes.getLong("id"));
                comments.add(com);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return comments;
    }

    public Comentario getComentario(long id_comentario){
        Connection con=null;
        Comentario com=null;
        try{
            String query = "SELECT * FROM COMENTARIO WHERE ID =?"; //TODO: revisar con modelo
            con = PuenteDB.getInstance().getConnection();
            PreparedStatement execQuery = con.prepareCall(query);
            execQuery.setLong(1,id_comentario);
            ResultSet comentarioRes = execQuery.executeQuery();
            if (comentarioRes.next()){
                com = new Comentario();
                com.setId(comentarioRes.getLong("id"));
                com.setArticulo(PuenteArt.getInstance().getArticulo(comentarioRes.getLong("articulo")));
                com.setAutor(PuenteUser.getInstance().getUser(comentarioRes.getLong("usuario")));
                com.setComentario(comentarioRes.getString("comentario"));
                com.setFecha(comentarioRes.getDate("fecha"));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return com;
    }
}
