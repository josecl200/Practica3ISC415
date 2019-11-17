package puentedb;

import clases.Articulo;
import clases.Comentario;
import clases.Etiqueta;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

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
            String query = "SELECT * FROM COMENTARIO WHERE ARTICULO = ? ORDER BY fecha";
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
            String query = "SELECT * FROM COMENTARIO WHERE ID = ?";
            con = PuenteDB.getInstance().getConnection();
            PreparedStatement execQuery = con.prepareCall(query);
            execQuery.setLong(1,id_comentario);
            ResultSet comentarioRes = execQuery.executeQuery();
            if (comentarioRes.next()){
                com = new Comentario();
                com.setId(comentarioRes.getLong("id"));
                com.setArticulo(comentarioRes.getLong("articulo"));
                com.setAutor(PuenteUser.getInstance().getUser(comentarioRes.getLong("autor")));
                com.setComentario(comentarioRes.getString("comentario"));
                com.setFecha(comentarioRes.getTimestamp("fecha").toLocalDateTime());
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try {
                assert con != null;
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return com;
    }

    public boolean crearComentario(Comentario com){
        Connection con = null;
        boolean ok = false;
        try {
            String query = "insert into comentario values(?,?,?,?,?)";
            con = PuenteDB.getInstance().getConnection();
            long uniqueID = UUID.randomUUID().getLeastSignificantBits();
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setLong(1, uniqueID);
            preparedStatement.setString(2, com.getComentario());
            preparedStatement.setLong(3, com.getAutor().getId());
            preparedStatement.setLong(4, com.getArticulo());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(com.getFecha()));

            int row = preparedStatement.executeUpdate();
            if (row>0)
                ok = true;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ok;
    }

    public boolean deleteComentario(long id_comentario) {
        boolean ok=false;
        Connection con = null;
        try {
            String query = "DELETE FROM COMENTARIO WHERE ID = ?";
            con = PuenteDB.getInstance().getConnection();
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setLong(1, id_comentario);


            int row = preparedStatement.executeUpdate();
            if (row>0)
                ok=true;


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ok;
    }
}
