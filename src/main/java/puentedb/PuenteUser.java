package puentedb;

import clases.Usuario;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class PuenteUser {

    private static PuenteUser instance;
    public static PuenteUser getInstance() {
        if(instance==null){
            instance=new PuenteUser();
        }
        return instance;
    }

    public boolean crearUsuario(Usuario usuario){
        boolean ok = false;
        Connection con = null;
        try{
            String query = "insert into USUARIO(id, username, nombre, password, administrator, autor) values (?,?,?,?,?,?)";
            con = PuenteDB.getInstance().getConnection();

            PreparedStatement preparedStatement = con.prepareStatement(query);

            long uniqueID = UUID.randomUUID().getLeastSignificantBits();

            preparedStatement.setLong(1, uniqueID);
            preparedStatement.setString(2, usuario.getUsername());
            preparedStatement.setString(3, usuario.getNombre());
            preparedStatement.setBytes(4, usuario.getPassword());
            preparedStatement.setBoolean(5, usuario.isAdmin());
            preparedStatement.setBoolean(6,usuario.isAutor());

            int row = preparedStatement.executeUpdate();
            ok = row > 0;
        } catch (SQLException ex) {

        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ok;
    }

    public ArrayList<Usuario> listarUsuario() {
        ArrayList<Usuario> users = new ArrayList<>();
        Connection con = null;
        try {
            String query = "select * from usuario";
            con = PuenteDB.getInstance().getConnection();
            PreparedStatement preparedStatement = con.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Usuario user = getUser(rs.getString("username"));
                users.add(user);
            }
        } catch (SQLException ex) {

        } finally {
            try {
                con.close();
            } catch (SQLException ex) {

            }
        }

        return users;
    }

    public Usuario getUser(String username) {
        Usuario user = null;
        Connection con = null;
        try {
            String query = "select * from usuario where username = ?";
            con = PuenteDB.getInstance().getConnection();

            PreparedStatement preparedStatement = con.prepareStatement(query);

            preparedStatement.setString(1, username);

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                user = new Usuario();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setNombre(rs.getString("nombre"));
                user.setPassword(rs.getBytes("password"));
                user.setAdmin(rs.getBoolean("admin"));
                user.setAutor(rs.getBoolean("autor"));
            }

        } catch (SQLException ex) {

        } finally {
            try {
                con.close();
            } catch (SQLException ex) {

            }
        }
        return user;
    }

    public Usuario getUser(Long id) {
        Usuario user = null;
        Connection con = null;
        try {
            String query = "select * from usuario where id = ?";
            con = PuenteDB.getInstance().getConnection();

            PreparedStatement preparedStatement = con.prepareStatement(query);

            preparedStatement.setLong(1, id);

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                user = new Usuario();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setNombre(rs.getString("nombre"));
                user.setPassword(rs.getBytes("password"));
                user.setAdmin(rs.getBoolean("admin"));
                user.setAutor(rs.getBoolean("autor"));
            }

        } catch (SQLException ex) {

        } finally {
            try {
                con.close();
            } catch (SQLException ex) {

            }
        }
        return user;
    }

    public boolean validateCredentials(String username, String password) throws NoSuchAlgorithmException {
        boolean login      = false;
        Connection con     = null;
        MessageDigest md   = MessageDigest.getInstance("SHA-224");
        byte[] hashPassEnt = md.digest(password.getBytes(StandardCharsets.UTF_8));

        try {
            String query = "select * from usuario where username = ?";
            con = PuenteDB.getInstance().getConnection();

            PreparedStatement preparedStatement = con.prepareStatement(query);

            preparedStatement.setString(1, username);

            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next())
                if(Arrays.equals(rs.getBytes("password"), hashPassEnt))
                    login=true;



        } catch (SQLException ex) {

        } finally {
            try {
                con.close();
            } catch (SQLException ex) {

            }
        }
        return login;
    }

}
