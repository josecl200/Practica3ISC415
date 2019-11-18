package puentedb;

import clases.Etiqueta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PuenteArtEtiqueta {

    private static PuenteArtEtiqueta instance;

    public static PuenteArtEtiqueta getInstance() {
        if(instance==null){
            instance = new PuenteArtEtiqueta();
        }
        return instance;
    }

    public ArrayList<Etiqueta> getEtiquetasArt(long idArt){
        ArrayList<Etiqueta> etiquetas = new ArrayList<>();
        Connection con = null;
        try{
            String query = "select * from artetiqueta where id_articulo = ?";
            con = PuenteDB.getInstance().getConnection();
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setLong(1,idArt);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()){
                Etiqueta etiqueta = PuenteEtiqueta.getInstance().getEtiqueta(rs.getLong("id_etiqueta"));
                etiquetas.add(etiqueta);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return etiquetas;
    }

    public boolean crearEtiquetasArt(long idArt, long idTag) {
        boolean ok = false;

        Connection con = null;

        try {
            String query = "insert into artetiqueta values(?,?)";
            con = PuenteDB.getInstance().getConnection();
            PreparedStatement preparedStatement = con.prepareStatement(query);

            preparedStatement.setLong(1, idArt);
            preparedStatement.setLong(2,idTag);

            int row = preparedStatement.executeUpdate();
            ok = row > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ok;
    }

    public boolean borrarEtiquetaArt(long idArt) {
        boolean ok = false;

        Connection con = null;

        try {
            String query = "delete from artetiqueta where id_articulo=?";
            con = PuenteDB.getInstance().getConnection();
            PreparedStatement preparedStatement = con.prepareStatement(query);

            preparedStatement.setLong(1, idArt);

            int row = preparedStatement.executeUpdate();
            ok = row > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ok;
    }
}
