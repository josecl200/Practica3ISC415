package puentedb;

import clases.Articulo;
import clases.Etiqueta;
import clases.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class PuenteArt {
    private static PuenteArt instance;

    public static PuenteArt getInstance(){
        if(instance==null){
            instance=new PuenteArt();
        }
        return instance;
    }

    public ArrayList<Articulo> cargarArticulos(){
        ArrayList<Articulo> arts = new ArrayList<>();
        Connection con=null;
        try{
            String query = "SELECT * FROM Articulo ORDER BY FECHA DESC ";
            con = PuenteDB.getInstance().getConnection();
            PreparedStatement execQuery = con.prepareCall(query);
            ResultSet articuloRes = execQuery.executeQuery();
            while (articuloRes.next()){
                Articulo art = getArticulo(articuloRes.getLong("id"));
                arts.add(art);
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

        return arts;
    }


    public Articulo getArticulo(long id) {
        Articulo art=null;
        Connection con=null;

        try {
            String query = "select * from Articulo where id = ?";
            con = PuenteDB.getInstance().getConnection();
            PreparedStatement preparedStatement = con.prepareStatement(query);

            preparedStatement.setLong(1, id);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                art = new Articulo();
                art.setId(rs.getLong("id"));
                art.setTitulo(rs.getString("titulo"));
                art.setCuerpo(rs.getString("cuerpo"));
                art.setFecha(rs.getTimestamp("fecha").toLocalDateTime());


                Usuario autor = PuenteUser.getInstance().getUser(rs.getLong("autor"));
                art.setAutor(autor);

                art.setListaComentarios(PuenteComentario.getInstance().cargarComentarios(id));

                art.setListaEtiquetas(PuenteArtEtiqueta.getInstance().getEtiquetasArt(id));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return art;
    }

    public long createArticulo(Articulo article) {
        long id = -1;
        Connection con = null;
        try {
            String query = "insert into articulo values(?,?,?,?,?)";
            con = PuenteDB.getInstance().getConnection();
            long uniqueID = UUID.randomUUID().getLeastSignificantBits();
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setLong(1, uniqueID);
            preparedStatement.setString(2, article.getTitulo());
            preparedStatement.setString(3, article.getCuerpo());
            preparedStatement.setLong(4, article.getAutor().getId());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(article.getFecha()));

            int row = preparedStatement.executeUpdate();
            if (row>0)
                id = uniqueID;

            for (Etiqueta tag : article.getListaEtiquetas()) {
                PuenteArtEtiqueta.getInstance().crearEtiquetasArt(uniqueID, tag.getId());
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return id;
    }

    public boolean modifyArticulo(Articulo article) {
        boolean ok = false;
        Connection con = null;
        try {
            String query = "UPDATE articulo SET (title,body,author_id,article_date) values(?,?,?,?) WHERE ID = ?";
            con = PuenteDB.getInstance().getConnection();
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setLong(5, article.getId());
            preparedStatement.setString(1, article.getTitulo());
            preparedStatement.setString(2, article.getCuerpo());
            preparedStatement.setLong(3, article.getAutor().getId());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(article.getFecha()));

            int row = preparedStatement.executeUpdate() ;
            if(row>0)
                ok=true;

            for (Etiqueta tag : article.getListaEtiquetas()) {
                if(PuenteEtiqueta.getInstance().getEtiqueta(tag.getEtiqueta())==null){
                    long idEt = PuenteEtiqueta.getInstance().crearEtiqueta(tag);
                    tag.setId(idEt);
                }else{
                    Etiqueta et = PuenteEtiqueta.getInstance().getEtiqueta(tag.getEtiqueta());
                    tag.setId(et.getId());
                }
            }
            for (Etiqueta tag : article.getListaEtiquetas()) {
                PuenteArtEtiqueta.getInstance().crearEtiquetasArt(article.getId(), PuenteEtiqueta.getInstance().getEtiqueta(tag.getEtiqueta()).getId());
            }

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

    public boolean deleteArticulo(long id_art) {
        boolean ok=false;
        Connection con = null;
        try {
            String query = "DELETE FROM ARTICULO WHERE ID = ?";
            con = PuenteDB.getInstance().getConnection();
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setLong(1, id_art);


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
