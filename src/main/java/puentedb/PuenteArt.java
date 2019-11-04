package puentedb;

import clases.Articulo;
import clases.Etiqueta;
import clases.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            String query = "SELECT * FROM Articulos ORDER BY fecha_publicacion"; //TODO: revisar con modelo
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

            while (rs.next()) {
                art = new Articulo();
                art.setId(rs.getLong("id"));
                art.setTitulo(rs.getString("titulo"));
                art.setCuerpo(rs.getString("cuerpo"));
                art.setFecha(rs.getTimestamp("fecha_publicacion"));

                Usuario autor = PuenteUser.getInstance().getUser(rs.getLong("id"));
                art.setAutor(autor);

                art.setListaComentarios(new ArrayList<>());

                art.setListaEtiquetas(PuenteArtEtiqueta.getInstance().getEtiquetasArt(art.getId()));
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
            String query = "insert into articles(uid,title,body,author_id,article_date) values(?,?,?,?,?)";
            con = PuenteDB.getInstance().getConnection();
            long uniqueID = UUID.randomUUID().getLeastSignificantBits();
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setLong(1, uniqueID);
            preparedStatement.setString(2, article.getTitulo());
            preparedStatement.setString(3, article.getCuerpo());
            preparedStatement.setLong(4, article.getAutor().getId());
            preparedStatement.setTimestamp(5, article.getFecha());

            int row = preparedStatement.executeUpdate();
            if (row>0){
                id = uniqueID;
            }

            ArrayList<Etiqueta> etiquetasUsadas = PuenteEtiqueta.getInstance().cargarEtiquetas();
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
                PuenteArtEtiqueta.getInstance().crearEtiquetasArt(uniqueID, PuenteEtiqueta.getInstance().getEtiqueta(tag.getEtiqueta()).getId());
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


}
