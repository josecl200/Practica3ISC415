package puentedb;


import clases.Etiqueta;
import org.h2.command.Prepared;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class PuenteEtiqueta {
    private static PuenteEtiqueta instance;

    public static PuenteEtiqueta getInstance() {
        if (instance==null){
            instance = new PuenteEtiqueta();
        }
        return instance;
    }

    public ArrayList<Etiqueta> cargarEtiquetas(){
        ArrayList<Etiqueta> etiquetas=new ArrayList<>();
        Connection con = null;
        try{
            String query = "SELECT * FROM ETIQUETA;";
            con = PuenteDB.getInstance().getConnection();
            PreparedStatement execQuery = con.prepareCall(query);
            ResultSet etiquetaRes = execQuery.executeQuery();
            while (etiquetaRes.next()){
                Etiqueta et = new Etiqueta();
                et.setId(etiquetaRes.getLong(1));
                et.setEtiqueta(etiquetaRes.getString(2));
                etiquetas.add(et);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally{
            try{
                con.close();
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
        return etiquetas;
    }

    public Etiqueta getEtiqueta(long id_etiqueta){
        Etiqueta et = null;
        Connection con = null;
        try{
            con = PuenteDB.getInstance().getConnection();
            String query = "SELECT * FROM Etiqueta where id = ?;";
            PreparedStatement etiqRes = con.prepareCall(query);
            etiqRes.setLong(1,id_etiqueta);
            ResultSet execQuery = etiqRes.executeQuery();
            if(execQuery.next()){
                et = new Etiqueta();
                et.setId(execQuery.getLong(1));
                et.setEtiqueta(execQuery.getString(2));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally{
            try{
                con.close();
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
        return et;
    }

    public Etiqueta getEtiqueta(String etiqueta){
        Etiqueta et = null;
        Connection con = null;
        try{
            con = PuenteDB.getInstance().getConnection();
            String query = "SELECT * FROM Etiqueta where etiqueta = ?;";
            PreparedStatement etiqRes = con.prepareCall(query);
            etiqRes.setString(1,etiqueta);
            ResultSet execQuery = etiqRes.executeQuery();
            if(execQuery.next()){
                et = new Etiqueta();
                et.setId(execQuery.getLong(1));
                et.setEtiqueta(execQuery.getString(2));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally{
            try{
                con.close();
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
        return et;
    }

    public long crearEtiqueta(Etiqueta et){
        boolean ok =false;
        Connection con = null;
        long id = -1;

        try{
            con = PuenteDB.getInstance().getConnection();
            String query = "INSERT INTO ETIQUETA VALUES (?,?)";
            PreparedStatement execQuery = con.prepareCall(query);
            long uniqueID = UUID.randomUUID().getLeastSignificantBits();
            execQuery.setLong(1,uniqueID);
            execQuery.setString(2, et.getEtiqueta());
            ResultSet insertRes = execQuery.executeQuery();
            if(insertRes.next()){
                id = uniqueID;
            }


        }catch (SQLException e){
            e.printStackTrace();
        }finally{
            try{
                con.close();
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
        return id;
    }




}
